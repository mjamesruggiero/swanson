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

(defresource transaction [id]
  :allowed-methods [:options :get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok (fn [_]
               (json/generate-string
                 (db/get-transaction (Integer/parseInt id)))))

(defn chart []
  (layout/common
    [:div {:id "chart-div"}]
    [:div {:id "table-div"}]
    [:script {:src "/js/app.js", :type "text/javascript"}]))

(defroutes transaction-routes
  (GET "/by-week" [] (by-week))
  (GET "/chart" [] (chart))
  (ANY "/transactions/:id" [id] (transaction id))
  (POST "/transaction" [] (post-transaction))
  (GET "/last-n/:limit" [limit] (last-n-transactions limit)))
