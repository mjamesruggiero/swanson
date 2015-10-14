(defproject swanson "0.1.0-SNAPSHOT"
  :description "Working with liberator"
  :url "http://mjamesruggiero.com"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [ring-server "0.3.1"]
                 [liberator "0.11.0"]
                 [cheshire "5.3.1"]
                 [lib-noir "0.7.2"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [org.clojure/data.csv "0.1.2"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler swanson.handler/app
         :init swanson.handler/init
         :destroy swanson.handler/destroy}
  :main swanson.start
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.3.1"]]}})
