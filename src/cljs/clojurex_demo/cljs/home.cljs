(ns clojurex-demo.cljs.home
  (:require [dommy.core :as d]
            [clojurex-demo.cljs.board :as b]
            [clojurex-demo.cljs.grid :refer [render-grid!]]
            [clojurex-demo.cljs.cells :refer [render-cells!]])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn canvas-node []
  (node
   (let [{:keys [blocks-tall blocks-wide]} b/canvas-size]
     [:canvas {:height (* b/block-size blocks-tall)
               :width (* b/block-size blocks-wide)}])))

(defn watch-hash! [!hash]
  (add-watch !hash :home-page
             (fn [_ _ _ hash]
               (d/replace-contents! (sel1 :#content) (node [:div.row {:style {:margin-top "2em"}}
                                                            [:div.col-md-6
                                                             (doto (canvas-node)
                                                               (render-grid!)
                                                               (render-cells!))]])))))
