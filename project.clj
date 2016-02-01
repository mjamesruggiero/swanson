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
                 [org.clojure/java.jdbc "0.3.0"]
                 [org.postgresql/postgresql "9.2-1003-jdbc4"]
                 [org.clojure/data.csv "0.1.2"]
                 [clj-time "0.8.0"]
                 [java-jdbc/dsl "0.1.0"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [cljs-http "0.1.18"]
                 [org.clojure/core.async "0.2.374"]]
  :plugins [[lein-ring "0.8.12"]
            [lein-cljsbuild "1.0.3"]
            [com.jakemccrary/lein-test-refresh "0.12.0"]]
  :ring {:handler swanson.handler/app
         :init swanson.handler/init
         :destroy swanson.handler/destroy
         :auto-reload? true
         :auto-refresh? true }
  :cljsbuild {
          :builds [{:source-paths ["src-cljs"]
                    :compiler {:output-to "resources/public/js/main.js"
                               :optimizations :whitespace
                               :pretty-print true }}]}
  :main swanson.start
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false,
     :stacktraces? false,
     :auto-reload? false}
    }
   :dev
   {:dependencies [[ring-mock "0.1.5"]
                   [ring/ring-devel "1.3.1"]
                   [clj-webdriver "0.7.1"]
                   [org.seleniumhq.selenium/selenium-server "2.47.0"]
                   [javax.servlet/servlet-api "2.5"]
                   [ring/ring-jetty-adapter "1.4.0"]]}})
