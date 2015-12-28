(ns swanson.categories
  (:require
    [clojure.browser.repl :as repl]
    [goog.net.XhrIo :as xhr]
    [goog.Uri.QueryData :as query-data]
    [goog.structs :as structs]
    [goog.dom :as dom]))

(enable-console-print!)

(defn categories-callback [reply-value]
  (println (.getResponseJson (.-target reply-value))))

(defn ajax-json [url]
  (xhr/send url categories-callback))

(defn categories
  [& _]
  (let [url "/categories/13"]
    (ajax-json url)))

(set! (.-onload js/window) categories)
