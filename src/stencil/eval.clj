(ns stencil.eval
  "converts Normalized Control AST -> Evaled token seq"
  (:require [stencil.infix :refer [eval-rpn]]
            [stencil.types :refer [control?]]))

(set! *warn-on-reflection* true)

(defmulti ^:private eval-step (fn [function data item] (or (:cmd item) [:tag (:tag item)])))

(defmethod eval-step :default [_ _ item] [item])

;; ha ez egy kep, aminek attributumai vannak

#_(defmethod eval-step [:tag :xmlns.http%3A%2F%2Fschemas.openxmlformats.org%2Fwordprocessingml%2F2006%2Fmain/drawing] [fun data item]
    (if-let [name-tag (some-> item (find-first :name) str .trim)]
      ;; ha a name fuggvenyhivasnak nez ki, akkor megprobaljuk vegrehajtani.
      ;; a generalt dokumentumba binariskent betesszuk a letrehozott kepfajlt
      ;; tovabba 

      ;; ha a name egy barcode(x) tipusu, akkor harapunk.
      (let [uuid (str (java.util.UUID/randomUUID))]
        - a barcode erteket be kell helyettesiteni a name helyere.
        -
        )
      [item]))

(defmethod eval-step :if [function data item]
  (if (eval-rpn data  function (:condition item))
    (mapcat (partial eval-step function data) (:then item))
    (mapcat (partial eval-step function data) (:else item))))

(defmethod eval-step :echo [function data item]
  (let [value (eval-rpn data function (:expression item))]
    [{:text (if (control? value) value (str value))}]))

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

;; TODO: creating image files for qr code or barcode should take place here