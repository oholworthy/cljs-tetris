(ns clojurex-demo.cljs.multiplayer-widget
  (:require [dommy.core :as d]
            [clojure.string :as s]
            [cljs.core.async :as a])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn player-name-form-node [bind-join!]
  (let [$name (node
               [:input.form-control {:type "text"
                                     :placeholder "Your name"
                                     :style {:width "15em"}}])]
    (node
     [:div
      [:form.form-inline {:onsubmit "return false;"}
       [:fieldset
        $name
        (doto (node [:button.btn.btn-primary {:style {:margin-left "1em"}} "Join"])
          (bind-join! $name))]]])))

(defn top-scores-node [top-scores]
  (when (seq top-scores)
    (node
     [:div
      [:table.table.table-striped.table-hover.table-condensed
       [:thead
        [:th "Player"] [:th {:style {:text-align :right}} "Score"]]
       [:tbody
        (for [{:keys [player score]} top-scores]
          (node
           [:tr
            [:td player] [:td {:style {:text-align :right}} score]]))]]])))

(defn multiplayer-node [bind-form! bind-join! bind-top-scores!]
  (node
   [:div {:style {:padding "1em"
                  :margin-top "1em"
                  :border "1px solid black"
                  :border-radius "1em"}}
    [:h4 "Multiplayer:"]
    (doto (node [:div {:style {:margin "1em"}}
                 (player-name-form-node bind-join!)])
      (bind-form!))
    (doto (node [:div {:style {:margin "1em"}}])
      (bind-top-scores!))]))

(defn join-binder [commands-ch]
  (fn bind-join! [$join-button $name]
    (d/listen! $join-button :click
               (fn [_]
                 (let [name (d/value $name)]
                   (when-not (s/blank? name)
                     (d/set-attr! $join-button :disabled true)
                     (a/put! commands-ch {:name name})))))))

(defn top-score-binder [!top-scores]
  (fn bind-top-scores! [$el]
    (add-watch !top-scores ::binder
               (fn [_ _ _ scores]
                 (d/replace-contents! $el (top-scores-node scores))))))

(defn form-binder [!player-name]
  (fn [$el]
    (letfn [(set-form-visibility! [name]
              (if-not (nil? name)
                (d/hide! $el)
                (d/show! $el)))]
      (add-watch !player-name ::form-visibility
                 (fn [_ _ _ name]
                   (set-form-visibility! name)))
      (set-form-visibility! @!player-name))))

(defn make-multiplayer-widget [!top-scores !player-name commands-ch]
  (def !test-top-scores !top-scores)
  (def !test-player-name !player-name)
  (def test-commands-ch commands-ch)

  (multiplayer-node (form-binder !player-name) (join-binder commands-ch) (top-score-binder !top-scores)))

(comment
  (reset! !test-top-scores [{:player "Bob" :score 14}
                            {:player "Steve" :score 8}
                            {:player "Chris" :score 5}
                            {:player "Tim" :score 2}])
  
  (reset! !test-player-name "James"))
