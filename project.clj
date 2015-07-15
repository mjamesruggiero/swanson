(defproject swanson "0.1.0-SNAPSHOT"
  :description "Experimenting with concurrency models"
  :url "http://mjamesruggiero.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [schejulure "0.1.4"]
                 [org.flatland/useful "0.10.0"]
                 [clj-http "0.7.0"]]
  :main swanson.core
  :profiles {:uberjar {:aot :all}})
