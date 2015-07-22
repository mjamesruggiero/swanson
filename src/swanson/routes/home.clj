(ns swanson.routes.home
  (:require [compojure.core :refer :all]
            [liberator.core :refer [defresource resource request-method-in]]
            [swanson.views.layout :as layout]))

(defn home []
  (layout/common [:h1 "Hello World!"]))

(defroutes home-routes
  (ANY "/" request
       (resource
         :handle-ok "Hello world!"
         :etag "fixed-etag"
         :available-media-types ["text-plain"])))
