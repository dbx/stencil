(ns stencil.util
  (:require [clojure.zip]))

(set! *warn-on-reflection* true)


(defn stacks-difference-key
  "Mindkey listanak levagja azt a kozos prefixet, amire a kulcsfuggveny ua az erteket adja."
  [key-fn stack1 stack2]
  (let [cnt (count (take-while true?
                               (map (fn [a b] (= (key-fn a) (key-fn b)))
                                    (reverse stack1) (reverse stack2))))]
    [(take (- (count stack1) cnt) stack1)
     (take (- (count stack2) cnt) stack2)]))


(def stacks-difference
  "mindket listanak levagja a kozos szuffixet"
  (partial identity stacks-difference-key))


(defn mod-stack-top-last
  "Egy stack legfelso elemenek legutolso elemet modositja.
   Ha nincs elem, IllegalStateException kivetelt dob."
  [stack f & args]
  (assert (list? stack))
  (assert (ifn? f))
  (conj (rest stack)
        (conj (pop (first stack))
              (apply f (peek (first stack)) args))))


(defn mod-stack-top-conj
  "Egy stack legfelso elemehez hozzafuz egy elemet"
  [stack & items]
  ; (assert (list? stack))
  (conj (rest stack) (apply conj (first stack) items)))


(defn update-peek
  "Egy stack legfelso elemet modositja."
  [xs f & args]
  ; (assert (list? xs))
  (assert (ifn? f))
  (conj (pop xs) (apply f (peek xs) args)))


(def extension->mime-type
  {"pdf"  "application/pdf"
   "html" "text/html"
   "rtf"  "application/rtf"
   "docx" "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
   "doc"  "application/msword"
   "odt"  "application/vnd.oasis.opendocument.text"})

(defn fixpt [f x] (let [fx (f x)] (if (= fx x) x (recur f fx))))
(defn zipper? [loc] (-> loc meta (contains? :zip/branch?)))
(defn iterations [f xs] (take-while some? (iterate f xs)))
(defn find-first [pred xs] (first (filter pred xs)))

(def xml-zip
  "Like clojure.zip/xml-zip but more flexible."
  ;; TODO: milyen modon jobb???
  (partial clojure.zip/zipper
           map?
           (comp seq :content)
           (fn [node children] (assoc node :content (and children (apply vector children))))))

(defn suffixes [xs] (take-while seq (iterate next xs)))
(defn prefixes [xs] (take-while seq (iterate butlast xs)))

(defn ->int [x]
  (cond (nil? x)    nil
        (string? x) (Integer/parseInt (str x))
        (number? x) (int x)
        :default    (assert false (format "Unexpected type %s of %s" (type x) (str x)))))

:OK
