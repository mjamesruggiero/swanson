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
