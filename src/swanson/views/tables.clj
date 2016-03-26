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
  ([categories uri transaction-id]
   (category-form categories uri transaction-id 2))
  ([categories uri transaction-id selected]
   [:form-to {:post uri :novalidate "" :role "form"}
    (form/drop-down {:class "form-control"}
                    (str "category-transaction-" transaction-id)
                    (map vals categories)
                    selected)]))

(defn- populated-category-form
  "closure that holds the categories and form URI"
  [c u] (partial category-form c u))

(defn- mk-transaction-row
  "converts transaction map to table row"
  [transaction category-fn]
  (let [id (first (keys transaction))
        row-as-vec (vec (flatten (vals transaction)))
        selected-category-id (nth row-as-vec 1 2)
        form (category-fn id selected-category-id)]
    (conj row-as-vec form)))

(defn- transactions->rows
  "helper that turns raw transactions into useful rows"
  [transactions categories url]
  (let [keyed-vectors (utils/maps->keyed-seq transactions :id)
        category-fn (populated-category-form categories url)]
    (map #(mk-transaction-row % category-fn) keyed-vectors)))

(defn transactions-with-category-form
  "list of transactions including category update form element"
  [transactions categories url]
  (layout/common
    [:script {:src "/js/transactions.js", :type "text/javascript"}]
    [:p {:id "foo-bar"}]
    [:div {:class "container"}
     (layout/panel-table "Transactions"
                         [:description :category_id :date :amount :id :category]
                         (transactions->rows transactions categories url))]))
