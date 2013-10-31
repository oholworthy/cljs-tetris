(ns clojurex-demo.cljs.cells
  (:require [dommy.core :as d]
            [clojurex-demo.cljs.board :as b])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn color-cell! [$canvas [x y] color]
  (let [context (.getContext $canvas "2d")]
    (set! (.-fillStyle context) color)
    (.fillRect context
               (inc (* x b/block-size))
               (inc (* y b/block-size))
               (- b/block-size 2)
               (- b/block-size 2))))

(defn color-cells! [$canvas cells color]
  (doseq [cell cells]
    (color-cell! $canvas cell color)))

(defn render-cells! [$canvas]
  (def $test-canvas $canvas))

