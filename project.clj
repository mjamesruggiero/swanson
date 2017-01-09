(defproject swanson "0.1.1"
  :description "Spend tracker"
  :url "http://mjamesruggiero.com"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.5.1"]
                 [hiccup "1.0.5"]
                 [ring-server "0.4.0"]
                 [liberator "0.14.1"]
                 [cheshire "5.6.3"]
                 [lib-noir "0.9.9"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.postgresql/postgresql "9.4.1212"]
                 [org.clojure/data.csv "0.1.3"]
                 [clj-time "0.13.0"]
                 [java-jdbc/dsl "0.1.3"]
                 [cljs-http "0.1.42"]
                 [ragtime  "0.6.3"]]
  :plugins [[lein-ring "0.8.12"]
            [com.jakemccrary/lein-test-refresh "0.12.0"]]
  :ring {:handler swanson.handler/app
         :init swanson.handler/init
         :destroy swanson.handler/destroy
         :auto-reload? true
         :auto-refresh? true }
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
                   [ring/ring-devel "1.5.0"]
                   [clj-webdriver "0.7.2"]
                   [org.seleniumhq.selenium/selenium-server "2.52.0"]
                   [javax.servlet/servlet-api "2.5"]
                   [ring/ring-jetty-adapter "1.5.0"]]}}

  :aliases  {"migrate" ["run"  "-m" "swanson.models.db/migrate"]
             "rollback" ["run" "-m" "swanson.models.db/rollback"]})
