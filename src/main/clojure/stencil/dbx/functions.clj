(ns stencil.dbx.functions
  "Custom function definitions"
  (:require [stencil.functions :as functions]))

(defmethod functions/call-fn "distinct" [_ elements]
  (vec (distinct elements)))