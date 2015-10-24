(ns swanson.handler
  (:require
    [compojure.core :refer [defroutes]]
    [compojure.route :as route]
    [noir.util.middleware :as noir-middleware]
    [swanson.routes.auth :refer [auth-routes]]
    [swanson.routes.home :refer [home-routes]]
    [swanson.routes.transactions :refer [transaction-routes]]))

(defn init []
  (println "swanson is starting"))

(defn destroy []
  (println "swanson is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (noir-middleware/app-handler
           [auth-routes
            home-routes
            transaction-routes
            app-routes]))
