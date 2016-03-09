(ns swanson.routes.home
  (:require [compojure.core :refer :all]
            [swanson.views.layout :as layout]
            [noir.session :as session]))

(defn home []
  (layout/common [:h1 "swanson" (session/get :user)]))

(defroutes home-routes
  (GET "/" [] (home)))
