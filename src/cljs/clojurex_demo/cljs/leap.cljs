(ns clojurex-demo.cljs.leap
  (:require [cljs.core.async :refer [put!]]))

(def last-movement (atom (.getTime (js/Date.))))

(defn valid-movement [vx t1]
  (let [t-diff (- t1 @last-movement)]
    (js/console.log t-diff)
    (js/console.log (pr-str (/ t-diff vx)))
    (and (> vx 50)
         (> (/ (* 10 t-diff) vx) 0.55)
         (> t-diff 75))))

(defn process-frame [frame command-ch]
  (if (> (-> frame .-pointables .-length) 0)
    ;; average x velocity of pointables
    (let [pointables (-> frame .-pointables js->clj)
          v (for [p pointables]
              (let [[vx vy vz] (.-tipVelocity p)]
                {:vx vx :vy vy :vz vz}))
          avg-vx (/ (reduce + (map :vx v)) (count v))
          abs-avg-vx (if (> avg-vx 0) avg-vx (* -1 avg-vx))
          t1 (.getTime (js/Date.))]
      (when (valid-movement abs-avg-vx t1)
        (reset! last-movement t1)
        (if (> avg-vx 0)
          (do (js/console.log "right")
              (put! command-ch :piece-right))
          (do (js/console.log "left")
              (put! command-ch :piece-left)))))))

(defn start
  [$canvas command-ch]
  (js/console.log "Leap start")
  (let [controller (Leap/Controller.)]
    (.on controller "deviceFrame"
         (fn [frame] (process-frame frame command-ch)))
    (.connect controller)))
