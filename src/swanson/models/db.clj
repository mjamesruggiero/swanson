(ns swanson.models.db
  (:require [clj-time.core :as time-core]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [java-jdbc.ddl :as ddl]
            [java-jdbc.sql :as sql]
            [swanson.models.matcher :as matcher]
            [swanson.models.category :as category]
            [swanson.utils :refer [db-config load-config date-converter]]
            [ragtime.jdbc :as migration-jdbc]
            [ragtime.repl :as repl])
  (:import [java.security MessageDigest]
           [javax.xml.bind DatatypeConverter]))

(def db-spec
  (db-config))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;          migrations
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn migration-config []
  {:datastore (migration-jdbc/sql-database db-spec)
   :migrations (migration-jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate (migration-config)))

(defn rollback []
  (repl/rollback (migration-config)))

(defn get-user
  [email]
  (let [user-seq (jdbc/query db-spec
                             (sql/select * :users (sql/where {:email email})))]
    (first user-seq)))

(defn create-user
  [fname lname email encrypted-pass]
  (jdbc/insert! db-spec
      :users
      [:fname :lname :email :encrypted_password]
      [fname lname email encrypted-pass]))
