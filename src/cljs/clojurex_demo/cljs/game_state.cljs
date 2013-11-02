(ns clojurex-demo.cljs.game-state
  (:require [clojurex-demo.cljs.board :as b]
            [clojurex-demo.cljs.tetraminos :as t]
            [cljs.core.async :as a]
            [clojure.set :as set])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn valid-starting-locations [{:keys [shape rotation] :as piece}]
  (let [cells (t/piece->cells (assoc piece :location [0 0]))
        {:keys [blocks-wide]} b/canvas-size
        ;; one row visible
        y (- (apply max (map second cells)))]

    ;; all of shape has to fit in width of canvas
    (for [valid-x (range (- (apply min (map first cells)))
                         (- blocks-wide (apply max (map first cells))))]
      [valid-x y])))

(defn random-piece []
  (let [shape (rand-nth (vec t/shapes))
        rotation (rand-int 4)]
    {:shape shape
     :color (rand-nth (vec t/colors))
     :rotation rotation
     :location (rand-nth (valid-starting-locations {:shape shape :rotation rotation}))}))

(defn new-game []
  {:current-piece (random-piece)})

(defn add-next-piece [game]
  (assoc game
    :current-piece (random-piece)
    :piece-placed? false))

;; ---------- COLLISION DETECTION ----------

(defn collision-cells [{:keys [placed-cells]}]
  ;; the set of cells that, if the piece occupies one of these, will
  ;; trigger a collision
  (let [{:keys [blocks-tall blocks-wide]} b/canvas-size]
    (set
     (concat
      ;; one cell for every column, at the bottom
      (for [x (range blocks-wide)]
        [x blocks-tall])

      ;; all the cells that have already been placed
      (map :cell placed-cells)))))

(defn piece-collision? [{:keys [current-piece] :as game}]
  (boolean (seq (set/intersection (collision-cells game)
                                  (set (t/piece->cells current-piece))))))

(defn place-piece [{:keys [current-piece placed-cells] :as game}]
  (let [new-placed-cells (concat placed-cells
                                (for [cell (t/piece->cells current-piece)]
                                  {:cell cell
                                   :color (:color current-piece)}))]
    (-> game
        (assoc :piece-placed? true)
        (assoc :placed-cells new-placed-cells)
        (dissoc :current-piece))))

;; ---------- TICK ----------

(defn ticker-ch [ms]
  (let [ch (a/chan)]
    (go-loop []
      (a/<! (a/timeout ms))
      (a/>! ch :tick)
      (recur))
    ch))

(defn apply-tick [{:keys [piece-placed?] :as game}]
  (let [new-game (update-in game [:current-piece :location 1] inc)]
    (cond
     piece-placed? (add-next-piece game)
     (piece-collision? new-game) (place-piece game)
     :otherwise new-game)))

(defn repeatedly-tick! [!game]
  (let [tick-ch (ticker-ch 500)]
    (go-loop []
      (<! tick-ch)
      (swap! !game apply-tick)
      (recur))))

;; ---------- COMMANDS ----------

(def movement-command?
  #{:piece-left :piece-right :piece-down
    :rotate-piece-clockwise :rotate-piece-anti-clockwise})

(defmulti calculate-new-position #(identity %2))

(defmethod calculate-new-position :piece-left [game _]
  (update-in game [:current-piece :location 0] dec))

(defmethod calculate-new-position :piece-right [game _]
  (update-in game [:current-piece :location 0] inc))

(defmethod calculate-new-position :rotate-piece-anti-clockwise [game _]
  (update-in game [:current-piece :rotation] inc))

(defmethod calculate-new-position :rotate-piece-clockwise [game _]
  (update-in game [:current-piece :rotation] dec))

(defmethod calculate-new-position :piece-down [game _]
  ;; TODO 
  game)

(defn valid-game? [{:keys [current-piece] :as new-game}]
  (let [cells (t/piece->cells current-piece)
        {:keys [blocks-wide]} b/canvas-size]
    (and (every? (fn [[x y]]
                   (and (< -1 x blocks-wide)))
                 cells)
         (not (piece-collision? new-game)))))

(defn apply-movement [game command]
  (let [new-game (calculate-new-position game command)]
    (if (valid-game? new-game)
      new-game
      game)))

(defn apply-command [{:keys [piece-placed?] :as game} command]
  (cond
   piece-placed? game
   (movement-command? command) (apply-movement game command)
   (= :new-game command) (new-game)))

(defn apply-commands! [!game command-ch]
  (go-loop []
    (let [command (<! command-ch)]
      (swap! !game apply-command command))
    (recur)))

;; ---------- WIRING UP ----------

(defn wire-up-state! [!game command-ch]
  (def !test-game !game)
  (def test-command-ch command-ch)

  (doto !game
    (reset! (new-game))
    (repeatedly-tick!)
    (apply-commands! command-ch)))
