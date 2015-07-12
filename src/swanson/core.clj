(ns swanson.core
  (:require
    [cheshire.core :as json]
    [clojure.edn :as edn]
    [compojure.core :refer :all]
    [compojure.handler :refer [site]]
    [ring.adapter.jetty :refer [run-jetty]]
    [ring.util.response :refer [response status]]))

(def snippets (repeatedly promise))

(future
  (doseq [snippet (map deref snippets)]
    (println snippet)))

(defn accept-snippet "enter snippets structure" [n text]
  (deliver (nth snippets n) text))

(defroutes app-routes
  (PUT "/snippet/:n" [n :as {:keys [body]}]
       (accept-snippet (edn/read-string n) (slurp body))
       (response "OK")))

(defn -main [& args]
  (run-jetty (site app-routes) {:port 3000}))
