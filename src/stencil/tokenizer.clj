(ns stencil.tokenizer
  "Fog egy XML dokumentumot es tokenekre bontja"
  (:require [clojure.data.xml :as xml]
            [stencil.infix :as infix]
            [stencil.types :refer :all]
            [stencil.merger :refer [map-actions-in-token-list]]
            [stencil.util :refer :all]))

(set! *warn-on-reflection* true)

;; TODO: itt mindengyiknel az operandust is at kell adni.
;; TODO: ahol algebrai kifejezes van, azt ki kell ertekelni. ehhez kellhet egy rendes parszer is.
(defn text->cmd [^String text]
  (assert (string? text))
  (let [text (.trim text)]
    (cond
      (#{"end" "endfor" "endif"} text) {:cmd :end}
      (= text "else") {:cmd :else}

      (.startsWith text "if ")
      {:cmd       :if
       :condition (infix/parse (.substring text 3))}

      (.startsWith text "for ")
      (let [[v expr] (vec (.split (.substring text 4) " in "))]
        {:cmd        :for
         :variable   (symbol (.trim ^String v))
         :expression (infix/parse expr)})

      (.startsWith text "=")
      {:cmd        :echo
       :expression (infix/parse (.substring text 1))}

      :otherwise (throw (ex-info "Unexpected command" {:command text})))))


(defn- structure->seq [parsed]
  (cond
    (string? parsed) [{:text parsed}] ; (split-text-token parsed)

    (map? parsed)    (if (seq (:content parsed))
                       (concat [(cond-> {:open (:tag parsed)}
                                  (seq (:attrs parsed)) (assoc :attrs (:attrs parsed)))]
                               (mapcat structure->seq (:content parsed))
                               [{:close (:tag parsed)}])
                       [(cond-> {:open+close (:tag parsed)}
                          (seq (:attrs parsed)) (assoc :attrs (:attrs parsed)))])
    :otherwise       (throw (ex-info "Unexpected node: " {:node parsed}))))


(defn- map-token [token] (if (:action token) (text->cmd (:action token)) token))


(defn parse-to-tokens-seq
  "Felparszolja az inputot mint token listat"
  [input]
  (let [parsed (xml/parse input)]
    (assert (map? parsed))
    (->> (structure->seq parsed)
         (map-actions-in-token-list)
         (map map-token))))


(defn- tokens-seq-reducer [stack token]
  (cond
    (:text token)
    (mod-stack-top-conj stack (:text token))

    (:open+close token)
    (let [elem (xml/element (:open+close token) (:attrs token))]
      (mod-stack-top-conj stack elem))

    (:open token)
    (let [elem (xml/element (:open token) (:attrs token))]
      (-> stack (mod-stack-top-conj elem) (conj [])))

    (:close token)
    (let [[s & stack] stack]
      ;; TODO: itt megnezhetnenk, hogy a verem tetejen milyen elem volt utoljara es ossze lehetne hasonlitani oket.
      (if (seq s)
        (mod-stack-top-last stack assoc :content s)
        stack))

    :default
    (throw (ex-info (str "Unexpected token!" token) {:token token}))))


(defn tokens-seq->document
  "Token listabol XML fat csinal."
  [tokens-seq]
  (let [result (reduce tokens-seq-reducer [()] tokens-seq)]
    (assert (= 1 (count result)))
    (assert (= 1 (count (first result))))
    (ffirst result)))

:OK
