(ns stencil.tree-postprocess-test
  (:require [clojure.zip :as zip]
            [stencil.types :refer :all]
            [clojure.test :refer [deftest is are testing]]
            [stencil.util :refer [xml-zip]]
            [stencil.tree-postprocess :refer :all]
            [stencil.postprocess.table :refer :all]))

(defn- table [& contents] {:tag "tbl" :content (vec contents)})
(defn- cell [& contents] {:tag "tc" :content (vec contents)})
(defn- row [& contents] {:tag "tr" :content (vec contents)})
(defn- cell-of-width [width & contents]
  {:tag "tc" :content (vec (list* {:tag "tcPr" :content [{:tag "gridSpan" :attrs {:xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/val (str width)}}]} contents))})

(deftest test-row-hiding-simple
  (testing "Second row is being hidden here."
    (is (=  (table (row (cell "first"))
                   (row (cell "third")))
            (postprocess (table (row (cell "first"))
                                (row (cell "second" (->HideTableRowMarker)))
                                (row (cell "third"))))))))


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
            {:tag :tblGrid
             :content [{:tag :gridCol :attrs {:xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/w "2000"}}
                       {:tag :gridCol :attrs {:xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/w "3000"}}]}
            (row (cell "X1") (cell "X3"))
            (row (cell "Y1") (cell "Y3")))
          (postprocess
           (table
            {:tag :tblGrid
             :content [{:tag :gridCol :attrs {:xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/w "2000"}}
                       {:tag :gridCol :attrs {:xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/w "4"}}
                       {:tag :gridCol :attrs {:xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/w "3000"}}]}
            (row (cell "X1") (cell "X2") (cell "X3"))
            (row (cell "Y1") (cell "Y2") (cell "Y3"))))))))

;; TODO: ennek az egesz tablaatot is at kellene mereteznie!!!!
(deftest test-column-cut
  (testing "We hide second column and expect cells to KEEP size"
    (is (= (table (row (cell-of-width 1 "X1") (cell-of-width 2 "X3"))
                  (row (cell-of-width 1 "Y1") (cell-of-width 2 "Y3")))
           (postprocess
             (table (row (cell-of-width 1 "X1") (cell-of-width 3 "X2" (->HideTableColumnMarker :cut)) (cell-of-width 2 "X3"))
                    (row (cell-of-width 1 "Y1") (cell-of-width 3 "Y2") (cell-of-width 2 "Y3"))))))))

(deftest resize-cut
  (is (=
        (table {:tag :tblGrid,
                :content [{:tag :gridCol, :attrs {ooxml-w "1000"}}
                          {:tag :gridCol, :attrs {ooxml-w "2000"}}]}
               (row) (row) (row))
         (zip/node
         (table-resize-widths
           (xml-zip (table {:tag :tblGrid
                            :content [{:tag :gridCol :attrs {ooxml-w "1000"}}
                                       {:tag :gridCol :attrs {ooxml-w "2000"}}
                                       {:tag :gridCol :attrs {ooxml-w "2500"}}
                                       {:tag :gridCol :attrs {ooxml-w "500"}}]}
                           (row) (row) (row)))
           :cut
           #{2 3})))))


(deftest resize-last
  (is (=
        (table
          {:tag :tblGrid,
           :content [{:tag :gridCol, :attrs {ooxml-w "1000"}}
                      {:tag :gridCol, :attrs {ooxml-w "5000"}}]}
          (row) (row) (row))
        (zip/node
          (table-resize-widths
            (xml-zip (table {:tag :tblGrid
                             :content [{:tag :gridCol :attrs {ooxml-w "1000"}}
                                        {:tag :gridCol :attrs {ooxml-w "2000"}}
                                        {:tag :gridCol :attrs {ooxml-w "2500"}}
                                        {:tag :gridCol :attrs {ooxml-w "500"}}]}
                            (row) (row) (row)))
            :resize-last
            #{2 3})))))


(deftest resize-rational
  (is (=
        (table
          {:tag :tblPr :content [{:tag :tblW, :attrs {ooxml-w "6000"}}]}
          {:tag :tblGrid,
           :content [{:tag :gridCol, :attrs {ooxml-w "2000"}}
                     {:tag :gridCol, :attrs {ooxml-w "4000"}}]}
          (row) (row) (row))
        (zip/node
          (table-resize-widths
            (xml-zip (table {:tag :tblPr
                             :content [{:tag :tblW :attrs {ooxml-w "?"}}]}

                            {:tag :tblGrid
                             :content [{:tag :gridCol :attrs {ooxml-w "1000"}}
                                       {:tag :gridCol :attrs {ooxml-w "2000"}}
                                       {:tag :gridCol :attrs {ooxml-w "2500"}}
                                       {:tag :gridCol :attrs {ooxml-w "500"}}]}
                            (row) (row) (row)))
            :rational
            #{2 3})))))

:OK
