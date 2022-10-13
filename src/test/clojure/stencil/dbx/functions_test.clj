(ns stencil.dbx.functions-test
  (:import [clojure.lang ExceptionInfo])
  (:require [stencil.functions :refer [call-fn]]
            [stencil.dbx.functions]
            [stencil.model]
            [clojure.test :refer [deftest testing is are]]))

(deftest test-distinct
  (is (= [1 2 3] (call-fn "distinct" [1 2 3])))
  (is (= ["a", "b", "c"] (call-fn "distinct" ["a" "a" "b" "c" "c" "a"]))))