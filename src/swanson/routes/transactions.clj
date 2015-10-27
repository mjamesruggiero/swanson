(ns swanson.routes.transactions
  (:require [compojure.core :refer :all]
            [cheshire.core :as json]
            [swanson.models.db :as db]
            [swanson.views.layout :as layout]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defn by-week []
  (json-response (db/get-transactions-by-week)))

(defn chart []
  (layout/common [:div {:id "chart_div"}]
                  [:script {:src "/js/app.js",
                            :type "text/javascript"}]))

(defroutes transaction-routes
  (GET "/by-week" [] (by-week))
  (GET "/chart" [] (chart)))
