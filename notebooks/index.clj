(ns index
  (:require [scicloj.kind-clerk.api :as kind-clerk]
            [tablecloth.api :as tc]
            [aerial.hanami.templates :as ht]
            [scicloj.noj.v1.vis :as vis]
            [scicloj.noj.v1.vis.python :as vis.python]
            [libpython-clj2.require :refer [require-python]]
            [libpython-clj2.python :refer [py. py.. py.-] :as py]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as fun]
            [scicloj.kindly.v3.api :as kindly]
            [scicloj.kindly.v3.kind :as kind]
            [hiccup.core :as hiccup]
            hiccup.util))


;; ## Adapt Clerk to Kindly
(kind-clerk/setup!)


;; ## SVG
(-> [:svg {:height 210
           :width 500}
     [:line {:x1 0
             :y1 0
             :x2 200
             :y2 200
             :style "stroke:rgb(255,0,0);stroke-width:2"}]]
    hiccup/html
    vis/raw-html)


;; ## Visualize datases with Hanami
(def dataset1
  (-> {:x (range 9)
       :y (map +
               (range 9)
               (repeatedly 9 rand))}
      tc/dataset))

(-> dataset1
    (vis/hanami-plot ht/point-chart
                     :MSIZE 200))

;; ## Interop

(require-python '[numpy :as np]
                '[numpy.random :as np.random]
                'matplotlib.pyplot
                '[seaborn :as sns]
                'json
                '[arviz :as az])

(def sine-data
  (-> {:x (range 0 (* 3 np/pi) 0.1)}
      tc/dataset
      (tc/add-column :y #(fun/sin (:x %)))))

(vis.python/with-pyplot
  ;; http://gigasquidsoftware.com/blog/2020/01/18/parens-for-pyplot/
  (matplotlib.pyplot/plot
   (:x sine-data)
   (:y sine-data)))

(vis.python/pyplot
 #(matplotlib.pyplot/plot
   (:x sine-data)
   (:y sine-data)))

;; https://seaborn.pydata.org/tutorial/introduction
(let [tips (sns/load_dataset "tips")]
  (sns/set_theme)
  (vis.python/pyplot
   #(sns/relplot :data tips
                 :x "total_bill"
                 :y "tip"
                 :col "time"
                 :hue "smoker"
                 :style "smoker"
                 :size "size")))

(let [size [10 50]
      data {:normal (apply np.random/randn size)
            :gumbel (np.random/gumbel :size size)
            :student_t (np.random/standard_t :df 6
                                             :size size)
            :exponential (np.random/exponential :size size)}]
  (vis.python/pyplot
   #(az/plot_forest data)))


:bye
