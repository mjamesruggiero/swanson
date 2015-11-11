(ns swanson.routes.transactions
  (:require [compojure.core :refer :all]
            [liberator.core :refer [resource defresource]]
            [cheshire.core :as json]
            [swanson.models.db :as db]
            [swanson.views.layout :as layout]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defn by-week []
  (json-response (db/get-transactions-by-week)))

(defresource post-transaction []
  :allowed-methods [:post]
  :available-media-types ["text/html"]
  :handle-ok (fn [ctx]
               (str "<html>Post text/plain to this resource.<br></html>"))
  :post! (fn [ctx]
           (dosync
             (let [body (slurp (get-in ctx [:request :body]))]
               (format (str "<html>you posted <pre>" body "</pre></html>")))))
  :post-redirect? (fn [ctx] {:location (format "/chart")}))

(defn chart []
  (layout/common
    [:div {:id "chart-div"}]
    [:div {:id "table-div"}]
    [:script {:src "/js/app.js", :type "text/javascript"}]))

(defroutes transaction-routes
  (GET "/by-week" [] (by-week))
  (GET "/chart" [] (chart))
  (POST "/transaction" [] (post-transaction)))
