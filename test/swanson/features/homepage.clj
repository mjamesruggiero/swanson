(ns swanson.features.homepage
  (:require [clojure.test :refer :all]
            [ring.adapter.jetty :refer [run-jetty]]
            [clj-webdriver.taxi :refer :all]
            [swanson.features.config :refer :all]
            [swanson.handler :refer [app]]))

(defn start-server []
  (loop [server (run-jetty app {:port test-port :join? false})]
  (if (.isStarted server)
    server
    (recur server))))

(defn stop-server [server]
  (.stop server))

(defn start-browser []
  (set-driver! {:browser :firefox}))

(defn stop-browser []
  (quit))

(deftest homepage-greeting
  (let [server (start-server)]
    (start-browser)
    (to test-base-url)
    (is (= (title) "swanson"))
    (stop-browser)
    (stop-server server)))
