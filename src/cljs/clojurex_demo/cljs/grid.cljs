(ns clojurex-demo.cljs.grid
  (:require [dommy.core :as d])
  (:require-macros [dommy.macros :refer [node sel1]]))

(defn render-grid! [$canvas]
  (def $test-canvas $canvas))
