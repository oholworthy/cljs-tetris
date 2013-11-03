(ns clojurex-demo.cljs.multiplayer-widget
  (:require [dommy.core :as d])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn player-name-form-node []
  (node
   [:div
    [:form.form-inline {:role "form" :onsubmit "return false;"}
     [:fieldset
      [:input.form-control {:type "text"
                            :placeholder "Your name"
                            :style {:width "15em"}}]
      [:button.btn.btn-primary {:style {:margin-left "1em"}} "Join"]]]]))

(def sample-top-scores
  [{:player "Bob" :score 14}
   {:player "Steve" :score 8}
   {:player "Chris" :score 5}
   {:player "Tim" :score 2}])

(defn top-scores-node [top-scores]
  (node
   [:div
    [:table.table.table-striped.table-hover.table-condensed
     [:thead
      [:th "Player"] [:th {:style {:text-align :right}} "Score"]]
     [:tbody
      (for [{:keys [player score]} top-scores]
        (node
         [:tr
          [:td player] [:td {:style {:text-align :right}} score]]))]]]))

(defn multiplayer-node []
  (node
   [:div {:style {:padding "1em"
                  :margin-top "1em"
                  :border "1px solid black"
                  :border-radius "1em"}}
    [:h4 "Multiplayer:"]
    [:div {:style {:margin "1em"}}
     (player-name-form-node)]
    [:div {:style {:margin "1em"}}
     (top-scores-node sample-top-scores)]]))

(defn make-multiplayer-widget [!top-scores !player-name commands-ch]
  (def !test-top-scores !top-scores)
  (def !test-player-name !player-name)
  (def test-commands-ch commands-ch)

  (multiplayer-node))
