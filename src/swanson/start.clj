(ns swanson.start
  (:use swanson.handler
        ring.server.standalone
        [ring.middleware file-info file])
  (:gen-class))

(defn get-handler []
  (-> #'app
    (wrap-file-info)))

(defn -main
  [& [port]]
  (let [port 8080]
    (serve (get-handler)
           {:open-browser? false
            :port port
            :init init
            :join true})))
