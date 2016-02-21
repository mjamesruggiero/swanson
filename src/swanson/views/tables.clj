(ns swanson.views.tables
  (:require
    [swanson.views.layout :as layout]
    [hiccup.element :refer [javascript-tag]]))

(defn summary [weeks recent]
  (layout/common
    [:script {:src "/js/app.js", :type "text/javascript"}]
    [:div {:class "container"}
     (layout/panel-table "By Week" [:total :date] weeks)]
    [:div {:class "container"} [:h2 "Last six weeks"]]
    [:div {:id "chart-div"}]
    [:div {:class "container"}
     (layout/panel-table "Recent" [:category :description :date :amount :id] recent)]))

(defn categories []
  (layout/common
    [:script {:src "/js/categories.js", :type "text/javascript"}]
    [:div {:class "container"} [:h2 "Categories YTD"]]
    [:div {:id "chart-div"}]))
