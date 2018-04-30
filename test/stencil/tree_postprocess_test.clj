(ns stencil.tree-postprocess-test
  (:require [clojure.zip :as zip]
            [stencil.types :refer :all]
            [clojure.test :refer [deftest is are testing]]
            [stencil.tree-postprocess :refer :all]))

(defn- table [& contents] {:tag "tbl" :content (vec contents)})

(defn- cell [& contents]
  {:tag "tc" :content (vec contents)})

(defn- row [& contents]
  {:tag "tr" :content (vec contents)})

(defn- cell-of-width [width & contents]
  {:tag "tc"
   :content (vec (list*
                   {:tag "tcPr" :content [{:tag "w:gridSpan" :attrs {"w:val" (str width)}}]}
                   contents
                   ))})

(def xml-2 {:tag "div"
           :content
                [
                 {:tag     "tbl"
                  :content [{:tag "tr" :content [(cell "x1")
                                                 (cell-of-width 2 (->HideTableColumnMarker) "x2+x3")]}
                            {:tag "tr" :content [(cell-of-width 2 "y1+y2")
                                                 (cell "y3")]}
                            {:tag "tr" :content [(cell-of-width 3 "z123")]}
                            {:tag "tr" :content [(cell "d1")
                                                 (cell "d2")
                                                 (cell "d3")]}]}]})


#_
(deftest test-column-merging-simple
  (testing "Second column is being hidden here."
    (is (=  (table (row (cell "x1"))
                   (row (cell "d1")))
            (postprocess (table (row (cell "x1") (cell (->HideTableColumnMarker)))
                                (row (cell "d1") (cell "d2"))))))))


(deftest test-column-merging-joined
  (testing "Second column is being hidden here."
    (is (=  (table (row (cell "x1") (cell "x3"))
                   (row (cell "d1") (cell "d2")))
            (postprocess (table (row (cell "x1") (cell (->HideTableColumnMarker) "x2") (cell "x3"))
                                (row (cell "d1") (cell-of-width 2 "d2"))))))))

;; TODO: szelesseg normalis kezelese!
