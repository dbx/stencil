(ns stencil.ignored-tag-test
  (:require [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [clojure.test :refer [deftest is are testing]]
            [stencil.tokenizer :as tokenizer]
            [stencil.postprocess.ignored-tag :refer :all]))

;; TODO: make xml namespace alias generating explicit
;; so that test cases can be written.

(def test-data-1
  (str
   "<?xml version='1.0' encoding='UTF-8'?>"
   "<aa:document xmlns:aa=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"
                   xmlns:b=\"http://schemas.openxmlformats.org/markup-compatibility/2006\"
                   xmlns:gh=\"http://github.com\"
                   xmlns:x=\"http://dbx.hu/1\"
                   x:teszt=\"teszt1\"
                   b:Ignorable=\"gh x\">"
   "<aa:body><aa:p xmlns:gh=\"http://dbx.hu/2\" gh:x=\"1\" b:Ignorable=\"gh x\"></aa:p></aa:body></aa:document>"))

(deftest test-ignored-tag-1
  (-> test-data-1
      (java.io.StringReader.)

      (tokenizer/parse-to-tokens-seq)
      (tokenizer/tokens-seq->document)

      (unmap-ignored-attr)
      (xml/emit-str)

      some?
      is))


:ok
