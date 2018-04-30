(ns stencil.tree-postprocess
  "XML fa utofeldolgozasat vegzo kod."
  (:require [clojure.zip :as zip]
            [stencil.types :refer [hide-table-column-marker?]]
            [stencil.util :refer :all]))

(set! *warn-on-reflection* true)

(defn- loc-cell?  [loc] (some-> loc zip/node :tag name #{"tc" "td" "th"}))
(defn- loc-row?   [loc] (some-> loc zip/node :tag name #{"tr"}))
(defn- loc-table? [loc] (some-> loc zip/node :tag name #{"tbl" "table"}))

(defn- find-first-in-tree [pred tree]
  (assert (zipper? tree))
  (assert (fn? pred))
  (find-first (comp pred zip/node) (take-while (complement zip/end?) (iterate zip/next tree))))

(defn- first-right-sibling
  "Megkeresi az elso aktualis poziciotol jobbra eso helyet, amire teljesul a feltetel."
  [pred loc] (find-first pred (iterations zip/right loc)))

(defn- first-parent
  "Megkeresi az elso szulo elemet, amire teljesul a feltetel."
  [pred loc]
  (assert (ifn? pred))
  (assert (zipper? loc))
  (find-first pred (iterations zip/up loc)))

(defn- find-enclosing-cell [loc] (first-parent loc-cell? loc))
(defn- find-enclosing-row [loc] (first-parent loc-row? loc))
(defn- find-enclosing-table [loc] (first-parent loc-table? loc))

(defn- find-closest-row-right [loc] (first-right-sibling loc-row? loc))
(defn- find-closest-cell-right [loc] (first-right-sibling loc-cell? loc))

(defn- goto-nth-sibling-cell [n loc]
  (assert (int? n))
  (assert (zipper? loc))
  (nth (filter loc-cell? (iterations zip/right (zip/leftmost loc))) n))

(defn- child-of-tag [tag-name loc]
  (assert (string? tag-name))
  (assert (zipper? loc))
  ;    (println :> (map zip/node (take-while some? (iterations zip/right (zip/down loc)))))
  (first (filter #(some-> % zip/node :tag name (= tag-name)) (take-while some? (iterations zip/right (zip/down loc))))))

(defn- cell-width
  "Az aktualis TD table cella szelesseget adja vissza. Alapertelmezetten 1."
  [loc]
  (assert (zipper? loc))
  (let [cell-loc      (find-enclosing-cell loc)
        cell          (zip/node cell-loc)]
    (case (name (:tag cell))
      ;; html
      ("td" "th") (-> cell :colspan ->int (or 1))

      ;; ooxml
      "tc" (or (some->> loc (child-of-tag "tcPr") (child-of-tag "w:gridSpan") zip/node :attrs (#(get % "w:val")) ->int) 1))))


(defn shrink-column
  "Az aktualis td cella szelesseget csokkenti"
  [col-loc shrink-amount]
  (assert (zipper? col-loc))
  (assert (pos? shrink-amount))

  (let [old-width (cell-width col-loc)]
    (assert (< shrink-amount old-width))
    ;; TODO: itt kell megoldani a frissitest
    (case (name (:tag (zip/node col-loc)))
      "td" (zip/edit col-loc update :width - shrink-amount)
      ("th" "tc")
      ;;  ez a resz atkozottul trukkos. kompatibilisnek kell maradni a cell-width fuggvennyel!
      col-loc
      )))


(defn- current-column-indices
  "Visszaadja egy halmazban, hogy hanyadik oszlop(ok)ban vagyunk benne eppen."
  [loc]
  (assert (zipper? loc))
  (let [cell         (find-enclosing-cell loc)
        current-cell-width (cell-width cell)
        cells-before (filter loc-cell? (next (iterations zip/left cell)))
        before-sum   (reduce + (map cell-width cells-before))]
    (set (for [i (range current-cell-width)] (+ before-sum i)))))

(defn remove-columns
  "Egy adott row alatt az adott sorszamu oszlopokat eltavolitja.
   Figyelembe veszi a cellak COLSPAN ertekeit, tehat ha egy cella
   tobb oszlopot is elfoglal, akkor elobb csak keskenyiti es csak akkor
   tavolitja el, ha a szelessege nulla lenne.

   Visszater a row lokatorral."
  [row-loc removable-columns]
  (assert (zipper? row-loc) "Elso parameter zipper legyen!")
  (let [row-loc           (find-enclosing-row row-loc)
        removable-columns (set removable-columns)]

    (assert (some? row-loc)         "Nem cell-ben vagyunk!")
    (assert (seq removable-columns) "Melyik oszlopokat tavolitsuk el?")

    (loop [current-loc (goto-nth-sibling-cell 0 (zip/down row-loc))
           current-idx 0]
      (let [column-width (cell-width current-loc)
            col-indices  (for [i (range column-width)] (+ current-idx i))
            last-column? (nil? (find-closest-cell-right (zip/right current-loc)))
            shrink-by    (count (filter removable-columns col-indices))]
        (if (pos? shrink-by)
          (if last-column?
            (if (= shrink-by column-width)
              (find-enclosing-row (zip/remove current-loc))
              (find-enclosing-row (shrink-column current-loc shrink-by)))
            (if (= shrink-by column-width)
              (recur (find-closest-cell-right (zip/next (zip/remove current-loc)))
                     (int (+ current-idx column-width)))
              (recur (find-closest-cell-right (zip/right (shrink-column current-loc shrink-by)))
                     (int (+ current-idx column-width)))))
          (if last-column?
            (find-enclosing-row current-loc)
            (recur (find-closest-cell-right (zip/right current-loc))
                   (int (+ current-idx column-width)))))))))

(defn- remove-current-column
  "A jelenlegi csomoponthoz tartozo oszlopot eltavolitja a tablazatbol.
   Visszater a gyoker elemmel."
  [start]
  (let [table          (find-enclosing-table start)
        column-indices (current-column-indices start)
        first-row      (find-closest-row-right (zip/leftmost (find-enclosing-row start)))]
    (loop [current-row first-row]
      (let [fixed-row (remove-columns current-row column-indices)]
        (if-let [next-row (some-> fixed-row zip/right find-closest-row-right)]
          (recur next-row)
          (zip/root fixed-row))))))

;; kulonbozo fazisok

(defn remove-columns-by-markers-1
  "Megkeresi az elso HideTableColumnMarkert es a tablazatbol a hozza tartozo
   oszlopot kitorli. Visszaadja az XML fat."
  [xml-tree]
  (if-let [marker (find-first-in-tree hide-table-column-marker? (xml-zip xml-tree))]
    (remove-current-column marker)
    xml-tree))

(defn remove-empty-table-rows-1 [xml-tree]
  ;; TODO: implement this
  xml-tree)

(defn remove-empty-tables-1 [xml-tree]
  ;; TODO: implement this
  xml-tree)

(defn deref-delayed-values
  "Vegigmegy a fan (melysegi bejarassal) es ha valahol DelayedValueMarker
   objektumot talal, kiertekeli azt es behelyettesiti az erteket a faba."
  [xml-tree]
  (loop [loc (xml-zip xml-tree)]
    (if (zip/end? loc)
      (zip/root loc)
      (if (instance? clojure.lang.IDeref (zip/node loc))
        (recur (zip/next (zip/edit loc deref)))
        (recur (zip/next loc))))))

(defn postprocess [xml-tree]
  (->> xml-tree
      (fixpt remove-columns-by-markers-1)
      (fixpt remove-empty-table-rows-1)
      (fixpt remove-empty-tables-1)
      (deref-delayed-values)))
