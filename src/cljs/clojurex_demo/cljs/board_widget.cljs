(ns clojurex-demo.cljs.board-widget
  (:require [dommy.core :as d]
            [clojurex-demo.cljs.board :as b]
            [clojurex-demo.cljs.cells :as c]
            [clojurex-demo.cljs.grid :refer [render-grid!]]
            [clojurex-demo.cljs.tetraminos :as t]
            [cljs.core.async :as a]
            [goog.events.KeyCodes :as kc])
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

(defn render-placed-cells! [$canvas {old-cells :placed-cells} {new-cells :placed-cells}]
  (when (not= old-cells new-cells)
    (when old-cells
      (c/color-cells! $canvas (map :cell old-cells) "white"))
    (when new-cells
      (doseq [{:keys [cell color]} new-cells]
        (c/color-cell! $canvas cell color)))))

(defn render-cleared-rows! [$canvas {:keys [cleared-rows]}]
  (let [{:keys [blocks-wide]} b/canvas-size]
    (when (seq cleared-rows)
      (doseq [y cleared-rows
              x (range blocks-wide)]
        (c/color-cell! $canvas [x y] "#ccc")))))

(defn bind-cell-flashing [$canvas cells-ch]
  (go-loop [cells []
            colour "red"]
    (let [[v c] (a/alts! [(a/timeout 600) cells-ch])]
      ;; not v if timeout, v if new cells list
      (if v
        (do
          (c/color-cells! $canvas cells "white")
          (recur v colour))
        (do
          (c/color-cells! $canvas cells colour)
          (recur cells ({"white" "red" "red" "white"} colour)))))))

(defn color-all! [$canvas color]
  (let [{:keys [blocks-wide blocks-tall]} b/canvas-size]
    (doseq [x (range blocks-wide)
            y (range blocks-tall)]
      (c/color-cell! $canvas [x y] color))))

(defn render-paused! [$canvas {old-paused? :paused?} {new-paused? :paused?
                                                      :keys [placed-cells current-piece]}]
  (when (and (not old-paused?)
             new-paused?)
    (color-all! $canvas "#888"))

  (when (and old-paused?
             (not new-paused?))
    (color-all! $canvas "white")
    (doseq [{:keys [cell color]} placed-cells]
      (c/color-cell! $canvas cell color))

    (when current-piece
      (t/render-tetramino! $canvas current-piece))))

(defn watch-game! [$canvas !game]
  (let [flashing-cells-ch (a/chan)]
    (bind-cell-flashing $canvas flashing-cells-ch)
    
    (add-watch !game ::renderer
               (fn [_ _ old-game new-game]
                 (when (and (:game-over? new-game) (not (:game-over? old-game)))
                   (a/put! flashing-cells-ch (map :cell (:placed-cells new-game))))

                 (when (and (:game-over? old-game) (not (:game-over? new-game)))
                   (a/put! flashing-cells-ch []))
               
                 (when-not (:game-over? new-game)
                   (render-current-piece! $canvas old-game new-game)
                   (render-placed-cells! $canvas old-game new-game)
                   (render-cleared-rows! $canvas new-game))

                 (render-paused! $canvas old-game new-game)))))

(def keycode->command
  {kc/SPACE :piece-down
   kc/LEFT :piece-left
   kc/RIGHT :piece-right
   kc/UP :rotate-piece-clockwise
   kc/DOWN :rotate-piece-anti-clockwise
   kc/N :new-game
   kc/P :toggle-pause})

(defn listen-for-keypresses! [$canvas command-ch]
  (d/listen! $canvas :keydown
             (fn [e]
               (when-let [command (keycode->command (.-keyCode e))]
                 (a/put! command-ch command)
                 (.preventDefault e)))))

(defn make-board-widget [!game command-ch]
  (def !test-game !game)
  (def test-command-ch command-ch)
  
  (doto (canvas-node)
    (render-grid!)
    (watch-game! !game)
    (listen-for-keypresses! command-ch)))

