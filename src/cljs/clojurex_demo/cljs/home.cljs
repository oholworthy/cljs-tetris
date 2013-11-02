(ns clojurex-demo.cljs.home
  (:require [dommy.core :as d]
            [clojurex-demo.cljs.board :as b]
            [clojurex-demo.cljs.grid :refer [render-grid!]]
            [clojurex-demo.cljs.board-widget :refer [make-board-widget]]
            [clojurex-demo.cljs.game-state :refer [wire-up-state!]]
            [cljs.core.async :as a])
  (:require-macros [dommy.macros :refer [node sel1]]
                   [cljs.core.async.macros :refer [go go-loop]]))

(defn watch-hash! [!hash]
  (add-watch !hash :home-page
             (fn [_ _ _ hash]
               (let [!game (atom {})
                     command-ch (a/chan)]

                 (d/replace-contents! (sel1 :#content)
                                      (node [:div.row {:style {:margin-top "2em"}}
                                             [:div.col-md-6
                                              (make-board-widget !game command-ch)]]))
                 (wire-up-state! !game command-ch)))))
