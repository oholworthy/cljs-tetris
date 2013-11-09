(ns clojurex-demo.cljs.game-state
  (:require [clojurex-demo.cljs.board :as b]
            [cljs.core.async :as a])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn new-game []
  )

(defn repeatedly-tick! [!game]
  )

(defn apply-commands! [!game command-ch]
  )

(defn wire-up-state! [!game command-ch]
  (def !test-game !game)
  (def test-command-ch command-ch)

  (doto !game
    (reset! (new-game))
    (repeatedly-tick!)
    (apply-commands! command-ch)))
