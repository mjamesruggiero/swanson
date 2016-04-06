(ns swanson.routes.home
  (:require [compojure.core :refer :all]
            [swanson.views.tables :as tables]
            [swanson.models.db :as db]
            [swanson.views.layout :as layout]
            [swanson.routes.transactions :as transactions]
            [noir.session :as session]))

(defn summary
  "summary page with weeks, months, and recents"
  []
  (let [months 6
        transaction-limit 25]
    (transactions/session-handler (tables/summary
                       (db/get-transactions-by-week)
                       (db/recent-transactions transaction-limit)
                       (db/last-n-months months)))))

(defroutes home-routes
  (GET "/" [] (summary)))
