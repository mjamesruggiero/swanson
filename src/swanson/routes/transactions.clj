(ns swanson.routes.transactions
  (:require [compojure.core :refer :all]
            [liberator.core :refer [resource defresource]]
            [cheshire.core :as json]
            [swanson.models.db :as db]
            [swanson.utils :as utils]
            [swanson.views.layout :as layout]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defn by-week []
  (json-response (db/get-transactions-by-week)))

(defn last-n-transactions [limit]
  (let [l (utils/parse-number limit 10)]
  (json-response (db/get-all-transactions l))))

(defresource post-transaction []
  :allowed-methods [:post]
  :available-media-types ["application/json"]
  :handle-ok ::data
  :post! (fn [ctx]
             (let [body (slurp (get-in ctx [:request :body]))]
               (db/create-transaction (utils/parse-transaction body))))
  :respond-with-entity? true)

(defresource update-transaction-cateogory [id]
  :allowed-methods [:put]
  :available-media-types ["application/json"]
  :handle-ok ::data
  :put! (fn [ctx]
             (let [body (slurp (get-in ctx [:request :body]))
                   parsed-id (utils/parse-number id)
                   parsed-transaction (utils/parse-transaction body)
                   trans-id (:id parsed-transaction)
                   new-category-id (:category_id parsed-transaction)]
               (println (str "about to PUT change to id " parsed-id " changing category id to " new-category-id))
               (db/update-category-id parsed-id new-category-id)))
  :respond-with-entity? true)

(defresource transaction [id]
  :allowed-methods [:options :get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok (fn [_]
               (json/generate-string
                 (db/get-transaction (Integer/parseInt id)))))

(defresource transactions []
  :allowed-methods [:options :get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok (fn [ctx]
              (let [default "25"
                    l (get-in ctx [:request :params :limit] default)
                    limit (utils/parse-number l)]
                (json-response (db/get-all-transactions limit)))))

(defn chart []
  (layout/common
    [:div {:id "chart-div"}]
    [:div {:id "table-div"}]
    [:script {:src "/js/app.js", :type "text/javascript"}]))

(defroutes transaction-routes
  (GET "/by-week" [] (by-week))
  (GET "/chart" [] (chart))
  (GET "/transactions/:id" [id] (transaction id))
  (PUT "/transactions/:id" [id] (update-transaction-cateogory id))
  (POST "/transactions" [] (post-transaction))
  (GET "/transactions" [] (transactions)))
