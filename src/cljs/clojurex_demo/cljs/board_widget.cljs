(ns clojurex-demo.cljs.board-widget
  (:require [dommy.core :as d]
            [clojurex-demo.cljs.board :as b]
            [clojurex-demo.cljs.cells :as c]
            [clojurex-demo.cljs.grid :refer [render-grid!]])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn canvas-node []
  (node
   (let [{:keys [blocks-tall blocks-wide]} b/canvas-size]
     [:canvas {:height (* b/block-size blocks-tall)
               :width (* b/block-size blocks-wide)}])))

(defn watch-game! [$canvas !game]
  (add-watch !game ::renderer
             (fn [_ _ old-game new-game]
               (js/console.log (pr-str {:old old-game
                                        :new new-game})))))

(defn make-board-widget [!game]
  (def !test-game !game)

  (doto (canvas-node)
    (render-grid!)
    (watch-game! !game)))
