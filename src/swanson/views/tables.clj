(ns swanson.views.tables
  (:require
    [swanson.views.layout :as layout]
    [hiccup.element :refer [javascript-tag]]))

(defn chart [weeks recent]
  (layout/common
    [:script {:src "/js/app.js", :type "text/javascript"}]
    [:div {:class "container"}
     (layout/panel-table "By Week" [:total :date] weeks)]
    [:div {:class "container"} [:h2 "Last six weeks"]]
    [:div {:id "chart-div"}]
    [:div {:class "container"}
     (layout/panel-table "Recent" [:category :description :date :amount :id] recent)]))
