(ns stencil.process
  "A konvertalas folyamat osszefogasa"
  (:gen-class)
  (:import [java.io File])
  (:require [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [stencil
             [tokenizer :as tokenizer]
             [cleanup :as cleanup]
             [eval :as eval]
             [tree-postprocess :as tree-postprocess]]
            [taoensso.timbre :refer [log trace debug info warn error fatal]]))

(set! *warn-on-reflection* true)

(defn- ->executable [readable]
  (with-open [r (io/reader readable)]
    (cleanup/process (tokenizer/parse-to-tokens-seq r))))

(defmulti prepare-template (fn [extension stream] (some-> extension name .trim .toLowerCase keyword)))

(defmethod prepare-template :default [extension _]
  ; (println "Unexpected extension: " extension)
  (throw (ex-info (str "Unrecognized extension: " extension)
                  {:extension extension})))

(defmethod prepare-template :xml [_ stream]
  (let [m (->executable stream)]
    {:variables  (:variables m)
     :type       :xml
     :executable (:executable m)}))

(defmethod  prepare-template :docx [suffix ^java.io.InputStream stream]
  ;; TODO: itt lehet h header es footer fajlt is kellene ertelmezni!
  (assert (some? suffix))
  (assert (some? stream))
  (let [zip-dir   (doto (File/createTempFile "stencil-" (str suffix ".zip.contents")) .delete)
        xml-list  ["word/document.xml"]
        execs     (delay (zipmap xml-list
                                 (for [d xml-list]
                                   (->executable (File. ^File zip-dir (str d))))))]
    (with-open [zip-stream stream]
      (io.github.erdos.stencil.impl.ZipHelper/unzipStreamIntoDirectory zip-stream zip-dir))
    {:zip-dir    zip-dir
     :type       :docx
     :variables  (set (mapcat :variables (vals @execs)))
     :exec-files (into {} (for [[k v] @execs] [k (:executable v)]))}))


(defn- run-executable-and-write [executable function data output-stream]
  (let [result (-> (eval/normal-control-ast->evaled-seq data function executable)
                   (tokenizer/tokens-seq->document)
                   (tree-postprocess/postprocess))
        writer (io/writer output-stream)]
    (xml/emit result writer)
    (.flush writer)))

(defmulti do-eval-stream (comp :type :template))

(defmethod do-eval-stream :docx [{:keys [template data function]}]
  (assert (:zip-dir template))
  (assert (:exec-files template))
  (let [data   (into {} data)
        {:keys [zip-dir exec-files]} template
        source-dir   (clojure.java.io/file zip-dir)
        pp           (.toPath source-dir)
        outstream    (new java.io.PipedOutputStream)
        input-stream (new java.io.PipedInputStream outstream)]
    (future
      (try
        (with-open [zipstream (new java.util.zip.ZipOutputStream outstream)]
          (doseq [file  (file-seq source-dir)
                  :when (not      (.isDirectory ^File file))
                  :let  [path     (.toPath ^File file)
                         rel-path (str (.relativize pp path))
                         ze       (new java.util.zip.ZipEntry rel-path)]]
            (.putNextEntry zipstream ze)
            (if-let [executable (get exec-files rel-path)]
              (run-executable-and-write executable function data zipstream)
              (java.nio.file.Files/copy path zipstream))
            (.closeEntry zipstream)))
        (catch Throwable e
          (println "Zipping exception: " e))))
    {:stream input-stream
     :format :docx}))
