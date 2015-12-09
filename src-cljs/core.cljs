(ns swanson.core
  (:require
    [clojure.browser.repl :as repl]
    [goog.net.XhrIo :as xhr]
    [goog.Uri.QueryData :as query-data]
    [goog.structs :as structs]
    [goog.dom :as dom]))

(enable-console-print!)

(defn ^:export main [& _]
  (println "starting!"))
