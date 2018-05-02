(ns stencil.tree-postprocess-test
  (:require [clojure.zip :as zip]
            [stencil.types :refer :all]
            [clojure.test :refer [deftest is are testing]]
            [stencil.tree-postprocess :refer :all]))

(defn- table [& contents] {:tag "tbl" :content (vec contents)})
(defn- cell [& contents] {:tag "tc" :content (vec contents)})
(defn- row [& contents] {:tag "tr" :content (vec contents)})
(defn- cell-of-width [width & contents]
  {:tag "tc" :content (vec (list* {:tag "tcPr" :content [{:tag "gridSpan" :attrs {:xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/val (str width)}}]} contents))})

(deftest test-column-merging-simple
  (testing "Second column is being hidden here."
    (is (=  (table (row (cell "x1"))
                   (row (cell "d1")))
            (postprocess (table (row (cell "x1") (cell (->HideTableColumnMarker)))
                                (row (cell "d1") (cell "d2"))))))))

(deftest test-column-merging-joined
  (testing "Second column is being hidden here."
    (is (=  (table (row (cell "x1") (cell "x3"))
                   (row (cell "d1") (cell-of-width 1 "d2")))
            (postprocess (table (row (cell "x1") (cell (->HideTableColumnMarker) "x2") (cell "x3"))
                                (row (cell "d1") (cell-of-width 2 "d2"))))))))

(deftest test-column-merging-super-complex
  (testing "Second column is being hidden here."
    (is (=
          (table
            (row (cell "H1") (cell "J1"))
            (row (cell-of-width 1 "H2 + I2") (cell "J2"))
            (row (cell "H") (cell-of-width 1 "I3 + J3"))
            (row (cell "H") (cell "J"))
            (row (cell-of-width 2 "F + G + H + I + J")))
          (postprocess
            (table
              (row (cell-of-width 2 "F1+G1" (->HideTableColumnMarker)) (cell "H1") (cell "I1" (->HideTableColumnMarker)) (cell "J1"))
              (row (cell "F2") (cell "G2") (cell-of-width 2 "H2 + I2") (cell "J2"))
              (row (cell-of-width 2 "F + G") (cell "H") (cell-of-width 2 "I3 + J3"))
              (row (cell "F") (cell "G") (cell "H") (cell "I") (cell "J"))
              (row (cell-of-width 5 "F + G + H + I + J"))))))))


(deftest test-column-merging-super-complex-2
  (testing "Second column is being hidden here."
    (is (=
          (table
            (row (cell "H1") (cell-of-width 2 "J1"))
            (row (cell-of-width 1 "H2 + I2") (cell-of-width 2 "J2"))
            (row (cell "H") (cell-of-width 2 "I3 + J3"))
            (row (cell "H") (cell-of-width 2 "J"))
            (row (cell-of-width 3 "F + G + H + I + J")))
          (postprocess
            (table
              (row (cell-of-width 2 "F1+G1" (->HideTableColumnMarker)) (cell "H1") (cell-of-width 2 "I1" (->HideTableColumnMarker)) (cell-of-width 2 "J1"))
               (row (cell "F2") (cell "G2") (cell-of-width 3 "H2 + I2") (cell-of-width 2 "J2"))
              (row (cell-of-width 2 "F + G") (cell "H") (cell-of-width 4 "I3 + J3"))
               (row (cell "F") (cell "G") (cell "H") (cell-of-width 2 "I") (cell-of-width 2 "J"))
              (row (cell-of-width 7 "F + G + H + I + J"))))))))


(deftest test-column-merging-super-complex-3
  (testing "Second column is being hidden here."
    (is (=
          (table (row (cell "X1") (cell "X3")) (row (cell "Y1") (cell "Y3")))
          (postprocess (table (row (cell "X1") (cell-of-width 2 "X2" (->HideTableColumnMarker)) (cell "X3"))
                              (row (cell "Y1") (cell-of-width 2 "Y2") (cell "Y3"))))))))

(deftest test-preprocess-remove-thin-cols
  (testing "There are infinitely thin columns that are being removed."
    (is (=
          (table
            {:tag :w:tblGrid
             :content [{:tag :w:gridCol :attrs {:xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/w "2000"}}
                       {:tag :w:gridCol :attrs {:xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/w "3000"}}]}
            (row (cell "X1") (cell "X3"))
            (row (cell "Y1") (cell "Y3")))
          (postprocess
            (table
              {:tag :w:tblGrid
               :content [{:tag :w:gridCol :attrs {:xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/w "2000"}}
                         {:tag :w:gridCol :attrs {:xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/w "4"}}
                         {:tag :w:gridCol :attrs {:xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/w "3000"}}]}
              (row (cell "X1") (cell "X2") (cell "X3"))
              (row (cell "Y1") (cell "Y2") (cell "Y3"))))))))


;; TODO: szelesseg normalis kezelese!
