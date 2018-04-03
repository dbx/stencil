(ns stencil.tree-postprocess-test
  (:import [stencil.types HideTableColumnMarker])
  (:require [clojure.zip :as zip]
            [stencil.types :as types]
            [clojure.test :refer [deftest is are testing]]
            [stencil.tree-postprocess :refer :all]))

(def xml1 {:tag "HEJHO"
           :content
           [
            {:tag "tbl"
             :content [{:tag "tr" :content [{:tag "tc" :content ["a"]}
                                            {:tag "tc" ::width 2
                                             :content ["d"
                                                       (HideTableColumnMarker.)
                                                       "1234" "e"]}]}
                       {:tag "tr" :content [{:tag "tc" ::width 2 :content ["x1"]}
                                            {:tag "tc" :content ["x2"]}]}
                       {:tag "tr" :content [{:tag "tc" ::width 3
                                             :content ["totalszeles"]}]}
                       {:tag "tr" :content [{:tag "tc" :content ["d1"]}
                                            {:tag "tc" :content ["d2"]}
                                            {:tag "tc" :content ["d3"]}]}]}]})


; (println (postprocess xml1))

;; TODO: szelesseg normalis kezelese!
