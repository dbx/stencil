(ns stencil.ignored-tag-test
  (:require [clojure.data.xml :as xml]
            [clojure.walk :as walk]
            [clojure.test :refer [deftest is are testing]]
            [stencil.tokenizer :as tokenizer]
            [stencil.postprocess.ignored-tag :refer :all]))

(deftest test-ignored-tag-1
  (->
   (str
    "<?xml version='1.0' encoding='UTF-8'?>"
    "<aa:document xmlns:aa=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"
                    xmlns:b=\"http://schemas.openxmlformats.org/markup-compatibility/2006\"
                    xmlns:gh=\"http://github.com\"
                    xmlns:x=\"http://dbx.hu/1\"
                    b:Ignorable=\"gh x\">"
    "<aa:body><aa:p xmlns:b=\"http://dbx.hu/2\" b:Ignorable=\"b\"></aa:p></aa:body></aa:document>")
   (java.io.StringReader.)
   (tokenizer/parse-to-tokens-seq)

    ;(unmap-ignored-attr)
;; do something here that just removes metadata completely
    ;;(->> (walk/postwalk #(if (map? %) (with-meta % {}) %)))

    ;(map-ignored-attr)

   (tokenizer/tokens-seq->document) (xml/emit-str)
   println))
