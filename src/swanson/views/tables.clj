(ns swanson.views.tables
  (:require
    [swanson.views.layout :as layout]
    [hiccup.element :refer [javascript-tag]]
    [hiccup.form :as form]))

(defn summary [weeks recent six-months]
  (layout/common
    [:script {:src "/js/app.js", :type "text/javascript"}]
    [:div {:class "container"}
     (layout/panel-table "By Week" [:total :date] weeks)]
    [:div {:class "container"} [:h2 "Last six weeks"]]
    [:div {:id "chart-div"}]
    [:div {:class "container"}
     (layout/panel-table "Recent" [:category :description :date :amount :id] recent)]
    [:div {:class "container"} [:h2 "Last six months"]]
    [:div {:id "six-months-chart-div"}]
    [:div {:class "container"}
     (layout/panel-table "Months" [:amount :month :year] six-months)]))

(defn categories []
  (layout/common
    [:script {:src "/js/categories.js", :type "text/javascript"}]
    [:div {:class "container"} [:h2 "Categories YTD"]]
    [:div {:id "chart-div"}]))

(defn category-form
  "generates form for category selection; category 2 is 'unknown'"
  ([categories transaction-id uri]
   (category-form categories transaction-id uri 2))
  ([categories transaction-id uri selected]
   [:div {:class  "well"}
    [:form-to {:post uri :novalidate "" :role "form"}
     [:div {:class "form-group"}
      (form/label {:class  "control-label"} "category"  "Category")
      (form/drop-down {:class "form-control"}
                      (str "category-transaction-" transaction-id)
                      categories
                      selected)]]]))
