(ns swanson.routes.home
  (:require [compojure.core :refer :all]
            [liberator.core :refer [defresource resource request-method-in]]
            [swanson.views.layout :as layout]))

(defresource home
  :service-available? false
  :handle-service-not-available "service is currently unavailable..."
  :method-allowed? (request-method-in :get)
  :handle-ok "Hello world!"
  :etag "fixed-etag"
  :available-media-types ["text-plain"])

(defroutes home-routes
  (ANY "/" request home))
