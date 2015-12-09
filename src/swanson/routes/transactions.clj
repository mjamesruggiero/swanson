(ns swanson.routes.transactions
  (:require [compojure.core :refer :all]
            [liberator.core :refer [resource defresource]]
            [swanson.models.db :as db]
            [swanson.utils :as utils]
            [swanson.views.layout :as layout]))

(defn by-week []
  (utils/json-response (db/get-transactions-by-week)))

(defn chart []
  (layout/common
    [:div {:id "chart-div"}]
    [:div {:id "table-div"}]
    [:script {:src "/js/app.js", :type "text/javascript"}]
    [:script {:src "/js/main.js", :type "text/javascript"}]
    [:button {:onclick "swanson.core.main()"}]))

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
               (db/update-category-id parsed-id new-category-id)))
  :respond-with-entity? true)

(defresource transaction [id]
  :allowed-methods [:options :get]
  :available-media-types ["text/html" "application/json"]
  :exists? (fn [ctx]
             (let [transaction (db/get-transaction (Integer/parseInt id))
                   transaction-exists? (not (empty? transaction))]
               (if transaction-exists?
                   {:transaction transaction})))
  :handle-ok (fn [ctx]
               (get ctx :transaction))
  :handle-not-found (fn [ctx]
                      (utils/json-response
                        {:error (str "Transaction not found for id " id)})))

(defresource transactions []
  :allowed-methods [:options :get]
  :available-media-types ["text/html" "application/json"]
  :handle-ok (fn [ctx]
              (let [default "25"
                    l (get-in ctx [:request :params :limit] default)
                    limit (utils/parse-number l)]
                (utils/json-response (db/get-all-transactions limit)))))

(defroutes transaction-routes
  (GET "/transactions" [] (transactions))                          ; index
  (POST "/transactions" [] (post-transaction))                     ; create
  (GET "/transactions/:id" [id] (transaction id))                  ; show
  (PUT "/transactions/:id" [id] (update-transaction-cateogory id)) ; update
  (GET "/by-week" [] (by-week))
  (GET "/chart" [] (chart)))
