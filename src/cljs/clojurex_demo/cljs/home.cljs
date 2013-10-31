(ns clojurex-demo.cljs.home
  (:require [dommy.core :as d]
            [clojurex-demo.cljs.board :as b]
            [clojurex-demo.cljs.board-widget :refer [make-board-widget]])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn watch-hash! [!hash]
  (add-watch !hash :home-page
             (fn [_ _ _ hash]
               (let [!game (atom {})]
                 (d/replace-contents! (sel1 :#content)
                                      (node [:div.row {:style {:margin-top "2em"}}
                                             [:div.col-md-6
                                              (make-board-widget !game)]]))))))
