(ns stencil.postprocess.ignored-tag
  "A docx fajlokban van egy Ignored tag, amiben xml namespace alias lista van.
   Ennek a tartalma kimenetkor is valodi xml ns aliasokat kell tartalmazzon."
  (:require [clojure.data.xml.pu-map :as pu-map]
            [clojure.string :as s]))

(def ^:private ignorable-tag :xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fmarkup-compatibility%2F2006/Ignorable)

(defn map-ignored-attr [xml-tree]
  (let [p->url    (get-in (meta xml-tree) [:clojure.data.xml/nss :p->u])
        path      [:attrs ignorable-tag]]
    (if (get-in xml-tree path)
      (update-in xml-tree path #(s/join " " (keep p->url (s/split (str %) #"\s+"))))
      xml-tree)))

(defn unmap-ignored-attr
  "A gyoker elemben az Ignored attributumba beir ertekeket"
  [xml-tree]
  (let [path [:attrs ignorable-tag]]
    (if-let [prefix->uri (some-> (get-in xml-tree path)
                                 (s/split #"\s+")
                                 (->> (zipmap (repeatedly (comp name gensym)))))]
      (-> xml-tree
          (assoc-in path (s/join " " (keys prefix->uri)))
          (with-meta {:clojure.data.xml/nss
                      (apply pu-map/assoc pu-map/EMPTY (flatten prefix->uri))}))
      xml-tree)))
