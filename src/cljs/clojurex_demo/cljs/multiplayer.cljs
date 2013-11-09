(ns clojurex-demo.cljs.multiplayer
  (:require [cljs.core.async :as a]
            [clojurex-demo.cljs.multiplayer-state :refer [wire-up-multiplayer!]]
            [clojurex-demo.cljs.multiplayer-widget :refer [make-multiplayer-widget]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn make-multiplayer-section [!game-state]
  (let [!top-scores (atom nil)
        !player-name (atom nil)
        commands-ch (a/chan)
        widget (make-multiplayer-widget !top-scores !player-name commands-ch)]
    (wire-up-multiplayer! !game-state !top-scores !player-name commands-ch)
    widget))
