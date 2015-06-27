(defproject swanson "0.1.0-SNAPSHOT"
  :description "Experimenting with concurrency models"
  :url "http://mjamesruggiero.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.5.1"]
                 [compojure "1.3.4"]
                 [ring "1.4.0-RC1"]
                 [cheshire "5.5.0"]
                 ]
  :main swanson.core
  :profiles {:uberjar {:aot :all}})
