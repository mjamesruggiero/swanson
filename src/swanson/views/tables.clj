(ns swanson.views.tables
  (:require
    [swanson.views.layout :as layout]
    [hiccup.element :refer [javascript-tag]]
    [hiccup.form :as form]
    [swanson.models.db :as db]
    [swanson.utils :as utils]))

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
   [:form-to {:post uri :novalidate "" :role "form"}
    (form/drop-down {:class "form-control"}
                    (str "category-transaction-" transaction-id)
                    (map vals categories)
                    selected)]))

(defn mk-keyed-vectors
  [l k]
  (map #(assoc {} (k %) (vals %) ) l))

(defn mk-category-form
  "converts transaction k->v to category form element"
  [transaction-rec categories url selected]
  (category-form categories (first (keys transaction-rec)) url selected))

(defn mk-transaction-row
  "converts transaction map to table row"
  [transaction categories form-url]
  (let [id (first (keys transaction))
        row-as-vec (vec (flatten (vals transaction)))
        selected-category-id (nth row-as-vec 1 2)
        form-element (mk-category-form transaction categories form-url selected-category-id)]
    (conj row-as-vec form-element)))

(defn transaction-rows
  "helper that turns raw transactions into useful rows"
  [transactions categories]
  (let [keyed-vectors (mk-keyed-vectors transactions :id)
        url "http://www.foo.com"]
    (map #(mk-transaction-row % categories url) keyed-vectors)))

(defn transactions-with-category-form
  "list of transactions including category update form element"
  [transactions categories]
  (layout/common
    [:div {:class "container"}
     (layout/panel-table "Transactions"
                         [:description :category_id :date :amount :id :category]
                         (transaction-rows transactions categories))]))
