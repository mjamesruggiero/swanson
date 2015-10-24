(ns swanson.routes.transactions
  (:require [compojure.core :refer :all]
            [cheshire.core :as json]
            [swanson.models.db :as db]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defn by-week []
  (json-response (db/get-transactions-by-week)))

(defroutes transaction-routes
  (GET "/by-week" [] (by-week)))
