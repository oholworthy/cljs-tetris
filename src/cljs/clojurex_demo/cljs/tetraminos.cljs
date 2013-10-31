(ns clojurex-demo.cljs.tetraminos
  (:require [dommy.core :as d]
            [clojurex-demo.cljs.board :as b]
            [clojurex-demo.cljs.cells :as c])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn render-tetraminos! [$canvas]
  (def $test-canvas $canvas))
