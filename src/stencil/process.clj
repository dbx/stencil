(ns stencil.process
  "A konvertalas folyamat osszefogasa"
  (:gen-class)
  (:import [java.io File PipedInputStream PipedOutputStream InputStream]
           [java.util.zip ZipEntry ZipOutputStream]
           [io.github.erdos.stencil.impl FileHelper ZipHelper])
  (:require [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [clojure.string :as s]
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

(defmulti prepare-template
  ;; extension: template file name extension
  ;; stream: template file contents
  (fn [extension stream] (some-> extension name .trim .toLowerCase keyword)))

(defmethod prepare-template :default [ext _]
  (throw (ex-info (format "Unrecognized extension: '%s'" ext) {:extension ext})))

(defmethod prepare-template :xml [_ stream]
  (let [m (->executable stream)]
    {:variables  (:variables m)
     :type       :xml
     :executable (:executable m)}))

(defmethod  prepare-template :docx [suffix ^InputStream stream]
  (assert (some? suffix))
  (assert (instance? InputStream stream))
  (let [zip-dir   (FileHelper/createNonexistentTempFile "stencil-" (str suffix ".zip.contents"))]
    (with-open [zip-stream stream] ;; FIXME: maybe not deleted immediately
      (ZipHelper/unzipStreamIntoDirectory zip-stream zip-dir))
    (let [xml-files (for [w (.list (File. zip-dir "word"))
                          :when (.endsWith (str w) ".xml")]
                      (str "word/" w))
          execs     (zipmap xml-files (map #(->executable (File. zip-dir (str %))) xml-files))]
      ;; TODO: maybe make it smarter by loading only important xml files
      ;; such as document.xml and footers/headers
      {:zip-dir    zip-dir
       :type       :docx
       :variables  (set (mapcat :variables (vals execs)))
       :exec-files (into {} (for [[k v] execs
                                  :when (:dynamic? v)]
                              [k (:executable v)]))})))

(defn unmap-ignored-attr
  "A gyoker elemben az Ignored attributumba beir ertekeket"
  [xml-tree]
  (let [path [:attrs :xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fmarkup-compatibility%2F2006/Ignorable]]
    (if-let [url->ps (some-> (get-in xml-tree path)
                             (s/split #"\s+")
                             (zipmap (repeatedly (comp vector name gensym))))]
      (-> xml-tree
          (assoc-in path (s/join " " (map first (vals url->ps))))
          ;; TODO: itt hasznaljuk a clojure.data.xml.pu-map ns kepessegeit!!!

          (with-meta {:clojure.data.xml/nss
                      (clojure.data.xml.pu-map/persistent! (reduce (fn [a [k v]] (clojure.data.xml.pu-map/assoc! a k v))
                                           (clojure.data.xml.pu-map/transient clojure.data.xml.pu-map/EMPTY)
                               (for [[url ps] url->ps
                                     p ps]
                                 [p url])))}))
      xml-tree)))

(defn- run-executable-and-write [executable function data output-stream]
  (let [result (-> (eval/normal-control-ast->evaled-seq data function executable)
                   (tokenizer/tokens-seq->document)
                   (tree-postprocess/postprocess)
                   (unmap-ignored-attr))
        writer (io/writer output-stream)]
    (xml/emit result writer)
    (.flush writer)))

(defmulti do-eval-stream (comp :type :template))

(defmethod do-eval-stream :docx [{:keys [template data function]}]
  (assert (:zip-dir template))
  (assert (:exec-files template))
  (let [data   (into {} data)
        {:keys [zip-dir exec-files]} template
        source-dir   (io/file zip-dir)
        pp           (.toPath source-dir)
        outstream    (new PipedOutputStream)
        input-stream (new PipedInputStream outstream)]
    (future
      (try
        (with-open [zipstream (new ZipOutputStream outstream)]
          (doseq [file  (file-seq source-dir)
                  :when (not      (.isDirectory ^File file))
                  :let  [path     (.toPath ^File file)
                         rel-path (str (.relativize pp path))
                         ze       (new ZipEntry rel-path)]]
            (.putNextEntry zipstream ze)
            (if-let [executable (get exec-files rel-path)]
              (run-executable-and-write executable function data zipstream)
              (java.nio.file.Files/copy path zipstream))
            (.closeEntry zipstream)))
        (catch Throwable e
          (println "Zipping exception: " e))))
    {:stream input-stream
     :format :docx}))

(defmethod do-eval-stream :xml [{:keys [template data function] :as input}]
  (assert (:executable template))
  (let [data         (into {} data)
        executable   (:executable template)
        out-stream   (new PipedOutputStream)
        input-stream (new PipedInputStream out-stream)]
    (future
      ;; TODO: itt hogyan kezeljunk hibat?
      (try
        (with-open [out-stream out-stream]
          (run-executable-and-write executable function data out-stream) )
        (catch Throwable e
          (println "Evaling exception: " e))))
    {:stream input-stream
     :format :xml}))
