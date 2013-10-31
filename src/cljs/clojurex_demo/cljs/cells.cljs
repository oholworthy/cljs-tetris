(ns clojurex-demo.cljs.cells
  (:require [dommy.core :as d]
            [clojurex-demo.cljs.board :as b])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn render-cells! [$canvas]
  (def $test-canvas $canvas))

(comment
  (let [$canvas $test-canvas
        [x y] [4 4]
        color "red"]

    
    (let [context (.getContext $canvas "2d")]
      (set! (.-fillStyle context) color)
      (.fillRect context
                 (* x b/block-size)
                 (* y b/block-size)
                 b/block-size
                 b/block-size))))
