(ns clojurex-demo.cljs.cells
  (:require [dommy.core :as d]
            [clojurex-demo.cljs.board :as b])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn render-cells! [$canvas]
  (def $test-canvas $canvas))
