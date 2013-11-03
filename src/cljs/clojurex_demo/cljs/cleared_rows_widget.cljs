(ns clojurex-demo.cljs.cleared-rows-widget
  (:require [dommy.core :as d])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn cleared-rows-node [cleared-count]
  (node
   [:div {:style {:margin-top "1em"}}
    [:strong "Cleared rows: "] cleared-count]))

(defn watch-game! [$el !game]
  (add-watch !game ::cleared-rows
             (fn [_ _ _ {:keys [cleared-row-count] :as game}]
               (d/replace-contents! $el (cleared-rows-node (or cleared-row-count 0))))))

(defn make-cleared-rows-widget [!game]
  (doto (node [:div (cleared-rows-node 0)])
    (watch-game! !game)))
