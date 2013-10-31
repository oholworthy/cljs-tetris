(ns clojurex-demo.cljs.grid
  (:require [dommy.core :as d]
            [clojurex-demo.cljs.board :as b])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn render-grid! [$canvas]
  (def $test-canvas $canvas))

(comment
  (let [$canvas $test-canvas]
    (let [context (.getContext $canvas "2d")
          {:keys [blocks-tall blocks-wide]} b/canvas-size]
      (doseq [i (range blocks-wide)
              j (range blocks-tall)]
        (.strokeRect context
                     (* i b/block-size)
                     (* j b/block-size)
                     b/block-size
                     b/block-size)))))
