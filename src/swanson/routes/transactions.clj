(ns swanson.routes.transactions
  (:require [compojure.core :refer :all]
            [liberator.core :refer [resource defresource]]
            [swanson.models.db :as db]
            [swanson.utils :as utils]
            [swanson.views.layout :as layout]
            [swanson.views.tables :as tables]
            [noir.session :as session]
            [hiccup.element :refer [javascript-tag]]
            [clojure.tools.logging :as log]))

(defresource post-transaction []
  :allowed-methods [:post]
  :available-media-types ["application/json"]
  :handle-ok ::data
  :post! (fn [ctx]
             (let [body (slurp (get-in ctx [:request :body]))]
               (db/create-transaction (utils/parse-transaction body))))
  :respond-with-entity? true)

(defresource update-transaction [id]
  :allowed-methods [:put]
  :available-media-types ["application/json"]
  :handle-ok ::data
  :put! (fn [ctx]
          (let [body (slurp (get-in ctx [:request :body]))
                parsed-id (utils/parse-number id)
                params (utils/parse-transaction body)
                category-id (utils/parse-number (:category_id params))]
            (db/update-category-id parsed-id category-id)))
  :respond-with-entity? true)

(defresource transaction [id]
  :allowed-methods [:options :get]
  :available-media-types ["application/json"]
  :exists? (fn [ctx]
             (let [transaction (db/get-transaction (Integer/parseInt id))
                   transaction-exists? (seq? transaction)]
               (if transaction-exists?
                   {:transaction transaction})))
  :handle-ok (fn [ctx]
               (get ctx :transaction))
  :handle-not-found (fn [ctx]
                      (utils/json-response
                        {:error (str "Transaction not found for id " id)})))

(def default-params
  {:months 6
   :transactions 25})

(defn by-week []
  (utils/json-response (db/get-transactions-by-week)))

(defn transactions []
  (let [limit (:transactions default-params)
        transactions (db/get-all-transactions limit)
        categories (db/all-categories)]
    (tables/transactions-with-category-form transactions categories)))

(defn uncategorized []
  (let [category (db/get-category "unknown")
        transactions (db/for-category (:id (first category)))
        categories (db/all-categories)]
    (tables/transactions-with-category-form transactions categories)))

(defn category [category-id]
  (let [result (db/category-monthly (Integer/parseInt category-id))]
    (utils/json-response result)))

(defn categories-ytd []
  (let [result (db/categories-ytd)]
    (utils/json-response result)))

(defn months []
  (let [result (db/last-n-months (:months default-params))]
    (utils/json-response result)))

(defn categories-last-month []
  (let [result (db/categories-last-month)]
    (utils/json-response result)))

(defn categories-handler [params]
  (let [scope (get params :scope)]
    (cond
      (= "ytd" scope) (categories-ytd)
      (= "all" scope) (tables/categories)
      (= "last-month" scope) (categories-last-month))))

(defn session-handler
  "return a status code when you are not authenticated"
  [& handler]
  (if (session/get :user)
    handler
    (layout/four-oh-one)))

(defroutes transaction-routes
  (GET "/transactions" [] (session-handler (transactions)))        ; index
  (POST "/transactions" [] (post-transaction))                     ; create
  (GET "/transactions/:id" [id] (transaction id))                  ; show
  (PUT "/transactions/:id" [id] (update-transaction id))           ; update
  (GET "/categories/:id" [id] (category id))                       ; show
  (GET "/categories" {params :params} []
       (categories-handler params))
  (GET "/by-week" [] (by-week))
  (GET "/months" [] (months))
  (GET "/uncategorized" [] (session-handler (uncategorized)))
  (GET "/summary" [] (session-handler (tables/summary
                      (db/get-transactions-by-week)
                      (db/recent-transactions (:transactions default-params))
                      (db/last-n-months (:months default-params))))))
