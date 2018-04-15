(ns stencil.eval
  "converts Normalized Control AST -> Evaled token seq"
  (:require [stencil.infix :refer [eval-rpn]]))

(set! *warn-on-reflection* true)

(def schema-tag Object)

(def schema-condition
  {:cmd :if
   :condition [Object]
   :then      [schema-tag]
   :else      [schema-tag]})

(def schema-loop
  {:cmd :for
   :variable String
   :expression [Object]
   :body-run-once [schema-tag]
   :body-run-none [schema-tag]
   :body-run-next [schema-tag]})

(def schema-echo
  {:cmd :echo
   :expression [Object]})

(defmulti ^:private eval-step (fn [function data item] (:cmd item)))

(defmethod eval-step nil [_ _ item] [item])

(defmethod eval-step :if [function data item]
  (if (eval-rpn data  function (:condition item))
    (mapcat (partial eval-step function data) (:then item))
    (mapcat (partial eval-step function data) (:else item))))

(defmethod eval-step :echo [function data item]
  [{:text (str (eval-rpn data function (:expression item)))}])

(defmethod eval-step :for [function data item]
  (if-let [items (seq (eval-rpn data function (:expression item)))]
    (let [datas  (map #(assoc data (name (:variable item)) %) items)
          bodies (cons (:body-run-once item) (repeat (:body-run-next item)))]
      (mapcat (fn [data body] (mapcat (partial eval-step function data) body)) datas bodies))
    (:body-run-none item)))

(defn normal-control-ast->evaled-seq [data function items]
  (assert (map? data))
  (assert (ifn? function))
  (mapcat (partial eval-step function data) items))