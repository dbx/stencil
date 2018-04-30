(ns stencil.types
  (:require [clojure.pprint])
  (:gen-class))

(set! *warn-on-reflection* true)

(def open-tag "{%")
(def close-tag "%}")

(defrecord OpenTag [open])
(defmethod clojure.pprint/simple-dispatch OpenTag [t] (print (str "<" (:open t) ">")))

(defrecord CloseTag [close])
(defmethod clojure.pprint/simple-dispatch CloseTag [t] (print (str "</" (:close t) ">")))

(defrecord TextTag [text])
(defmethod clojure.pprint/simple-dispatch TextTag [t] (print (str "'" (:text t) "'")))

(defn ->text [t] (->TextTag t))
(defn ->close [t] (->CloseTag t))
(def ->open ->OpenTag)

;; egyedi parancs objektumok

;; ez a marker jeloli, hogy egy oszlopot el kell rejteni.
(defrecord HideTableColumnMarker [])

(defn hide-table-column-marker? [x] (instance? HideTableColumnMarker x))

;; ez a marker valamilyen kesleltetett erteket jelol.
(defrecord DelayedValueMarker [delay-object]
  clojure.lang.IDeref
  (deref [_] @delay-object))

(defmulti control? type)
(defmethod control? :default [_] false)
(defmethod control? HideTableColumnMarker [_] true)