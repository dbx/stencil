(ns stencil.postprocess.ignored-tag
  "A docx fajlokban van egy Ignored tag, amiben xml namespace alias lista van.
   Ennek a tartalma kimenetkor is valodi xml ns aliasokat kell tartalmazzon."
  (:require [clojure.data.xml.pu-map :as pu-map]
            [clojure.string :as s]))

(def ^:private ignorable-tag :xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fmarkup-compatibility%2F2006/Ignorable)

;; like clojure.walk/postwalk but keeps metadata and calls fn only on nodes
(defn- postwalk-xml [f xml-tree]
  (if (map? xml-tree)
    (f (update xml-tree :content (partial mapv (partial postwalk-xml f))))
    xml-tree))

(defn- url-decode [s] (java.net.URLDecoder/decode (str s) "UTF-8"))

(defn- collect-all-nss [form]
  (->> form
       (tree-seq map? :content)
       (mapcat #(when (map? %) (list* (:tag %) (keys (:attrs %)))))
       (keep namespace)
       (keep #(when (.startsWith (str %) "xmlns.") (.substring (str %) 6)))
       (map url-decode)
       (into (sorted-set))))

(defn- map-str [f s] (s/join " " (keep f (s/split s #"\s+"))))

(defn- gen-alias [] (name (gensym "xml")))

;; first call this
(defn map-ignored-attr
  "A gyoker elemben az Ignored attributumba beir ertekeket
   Deszerializalas utan rogton hivjuk az xml fan."
  [xml-tree]
  (postwalk-xml (fn [form]
                  (if (contains? (:attrs form) ignorable-tag)
                    (let [p->url (get-in (meta form) [:clojure.data.xml/nss :p->u])]
                      (update-in form [:attrs ignorable-tag] (partial map-str p->url)))
                    form))
                xml-tree))

;; last call this
(defn unmap-ignored-attr
  "A munka vegeztevel, szerializalas elott hivjuk."
  [xml-tree]
  (let [all-nss (collect-all-nss xml-tree)
        found (volatile! {}) ;; uri -> alias list
        prefix->uri (delay (zipmap (vals @found) (keys @found)))
        find! (fn [uri] (when (all-nss uri)
                          (or (get @found uri)
                              (get (vswap! found assoc uri (gen-alias)) uri))))
        make-nss-map #(apply pu-map/assoc pu-map/EMPTY (interleave (vals @found) (keys @found)))]
    (->
     (postwalk-xml (fn [form]
                     (if (contains? (:attrs form) ignorable-tag)
                       (update-in form [:attrs ignorable-tag] (partial map-str find!))
                       form))
                   xml-tree)
     (with-meta {:clojure.data.xml/nss (make-nss-map)}))))

:OK
