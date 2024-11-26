(ns noj-book.tokyo-railways
  [:require
   [clojure.java.io :as io]
   [clojure.data.json :as json]
   [scicloj.clay.v2.api :as clay]
   [scicloj.kindly.v4.kind :as kind]])

(def cytoscape-example
  (kind/cytoscape
   {:elements {:nodes [{:data {:id "a" :parent "b"} :position {:x 215 :y 85}}
                       {:data {:id "b"}}
                       {:data {:id "c" :parent "b"} :position {:x 300 :y 85}}
                       {:data {:id "d"} :position {:x 215 :y 175}}
                       {:data {:id "e"}}
                       {:data {:id "f" :parent "e"} :position {:x 300 :y 175}}]
               :edges [{:data {:id "ad" :source "a" :target "d"}}
                       {:data {:id "eb" :source "e" :target "b"}}]}
    :style [{:selector "node"
             :css {:content "data(id)"
                   :text-valign "center"
                   :text-halign "center"}}
            {:selector "parent"
             :css {:text-valign "top"
                   :text-halign "center"}}
            {:selector "edge"
             :css {:curve-style "bezier"
                   :target-arrow-shape "triangle"}}]
    :layout {:name "preset"
             :padding 5}}))

;; is limited to about 3000 lines of json; full is 500,000 lines
(def tokyo-railways
  (with-open [rdr (io/reader "data/tokyo-railways/tokyo-railways.json")]
    (-> rdr
        (json/read {:key-fn keyword})
        (select-keys [:elements])
        (conj {:style [{:selector "node"
                        :css {:content "data(id)"
                              :text-valign "center"
                              :text-halign "center"}}
                       {:selector "parent"
                        :css {:text-valign "top"
                              :text-halign "center"}}
                       {:selector "edge"
                        :css {:curve-style "bezier"
                              :target-arrow-shape "triangle"}}]
               :layout {:name "preset" :padding 5}})
        (kind/cytoscape))))

(defn run [opts]
  ;; (clay/make! {:single-form 'tokyo-railways})
  (clay/make! {:single-value tokyo-railways})
  )
