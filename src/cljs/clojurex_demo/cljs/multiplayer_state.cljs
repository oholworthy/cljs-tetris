(ns clojurex-demo.cljs.multiplayer-state
  (:require [cljs.core.async :as a]
            [chord.client :refer [ws-ch]]
            [cljs.reader :refer [read-string]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def ws-url
  (let [loc js/location]
    (str "ws://" (.-host loc) "/scores")))

(defn send-scores! [name !game-state scores-ch]
  (a/put! scores-ch {:player name :score (or (:cleared-row-count @!game-state) 0)})
  (add-watch !game-state ::multiplayer
             (fn [_ _ old-state new-state]
               (let [{old-count :cleared-row-count} old-state
                     {new-count :cleared-row-count} new-state]
                 (when (not= old-count new-count)
                   (a/put! scores-ch {:player name :score (or new-count 0)}))))))

(defn watch-other-scores! [scores-ch !top-scores]
  (go-loop []
    (reset! !top-scores (read-string (:message (a/<! scores-ch))))
    (recur)))

(defn wire-up-multiplayer! [!game-state !top-scores !player-name commands-ch]
  (go
    (let [{:keys [name]} (a/<! commands-ch)
          scores-ch (a/<! (ws-ch ws-url))]
      (send-scores! name !game-state scores-ch)
      (when (= :success (read-string (:message (a/<! scores-ch))))
        (watch-other-scores! scores-ch !top-scores)
        (reset! !player-name name)))))

