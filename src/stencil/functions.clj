(ns stencil.functions
  "Function definitions"
  (:require [stencil.types :refer :all]))

(set! *warn-on-reflection* true)

(defmulti call-fn (fn [fname & args-seq] fname))

(defmethod call-fn "str" [_ & args-seq] (apply str args-seq))

(defmethod call-fn "range"
  ([_ x] (range x))
  ([_ x y] (range x y))
  ([_ x y z] (range x y z)))

;; finds first nonempy argument
(defmethod call-fn "coalesce" [_ & args-seq]
  (some #(when-not (empty? %) %) args-seq))

;; TODO: itt valami ellenorzes, hogy megfelelo szamu parameter van?
(defmethod call-fn "format" [_ format-string & args]
  (try (String/format (str format-string) (to-array args))
       (catch java.util.UnknownFormatConversionException e
         (throw (ex-info "Unknown format conversion: "
                         {:msg (.getMessage e)})))
       (catch java.util.MissingFormatArgumentException e
         (throw (ex-info "Missing format argument"
                         {:msg (.getMessage e)})))))

(defmethod call-fn "length" [_ items] (count items))