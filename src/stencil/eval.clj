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

(defmulti ^:private eval-step (fn [data item] (:cmd item)))

(defmethod eval-step nil [data item] [item])

(defmethod eval-step :if [data item]
  (if (eval-rpn data (:condition item))
    (mapcat (partial eval-step data) (:then item))
    (mapcat (partial eval-step data) (:else item))))

(defmethod eval-step :echo [data item]
  [{:text (str (eval-rpn data (:expression item)))}])

(defmethod eval-step :for [data item]
  (if-let [items (seq (eval-rpn data (:expression item)))]
    (let [datas  (map #(assoc data (name (:variable item)) %) items)
          bodies (cons (:body-run-once item) (repeat (:body-run-next item)))]
      (mapcat (fn [data body] (mapcat (partial eval-step data) body)) datas bodies))
    (:body-run-none item)))

(defn normal-control-ast->evaled-seq [data items]
  (mapcat (partial eval-step data) items))

;; (normal-control-ast->evaled-seq {} [{:text "a"}])f
