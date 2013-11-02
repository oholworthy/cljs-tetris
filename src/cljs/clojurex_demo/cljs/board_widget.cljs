(ns clojurex-demo.cljs.board-widget
  (:require [dommy.core :as d]
            [clojurex-demo.cljs.board :as b]
            [clojurex-demo.cljs.cells :as c]
            [clojurex-demo.cljs.grid :refer [render-grid!]]
            [clojurex-demo.cljs.tetraminos :as t]
            [cljs.core.async :as a])
  (:require-macros [dommy.macros :refer [node sel1]]
                   [cljs.core.async.macros :refer [go go-loop]]))

(defn canvas-node []
  (node
   (let [{:keys [blocks-tall blocks-wide]} b/canvas-size]
     [:canvas {:height (* b/block-size blocks-tall)
               :width (* b/block-size blocks-wide)
               :tabindex 0}])))

(defn render-current-piece! [$canvas {old-piece :current-piece} {new-piece :current-piece}]
  (when (not= old-piece new-piece)
    (when old-piece
      (t/render-tetramino! $canvas (assoc old-piece :color "white")))
    (when new-piece
      (t/render-tetramino! $canvas new-piece))))

(defn watch-game! [$canvas !game]
  (add-watch !game ::renderer
            (fn [_ _ old-game new-game]
              (render-current-piece! $canvas old-game new-game))))

(defn listen-for-keypresses! [$canvas command-ch]
  )

(defn make-board-widget [!game command-ch]
  (def !test-game !game)
  (def test-command-ch command-ch)
  
  (doto (canvas-node)
    (render-grid!)
    (watch-game! !game)
    (listen-for-keypresses! command-ch)))

(comment
  (reset! !test-game
          (let [{:keys [blocks-wide blocks-tall]} b/canvas-size]
            {:current-piece {:shape (rand-nth (vec t/shapes))
                             :color (rand-nth (vec t/colors))
                             :rotation (rand-int 4)
                             :location (map rand-int [blocks-wide blocks-tall])}})))
