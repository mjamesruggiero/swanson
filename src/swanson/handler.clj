(ns swanson.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [swanson.routes.home :refer [home-routes]]
            [swanson.routes.auth :refer [auth-routes]]))

(defn init []
  (println "swanson is starting"))

(defn destroy []
  (println "swanson is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (-> (routes auth-routes home-routes app-routes) handler/site))
