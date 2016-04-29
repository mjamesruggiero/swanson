(ns swanson.models.user
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [java-jdbc.ddl :as ddl]
            [java-jdbc.sql :as sql]
            [swanson.utils :refer [db-config]])
  (:import [java.security MessageDigest]
           [javax.xml.bind DatatypeConverter]))

(def db-spec
  (db-config))

(defn by-email
  [email]
  (let [user-seq (jdbc/query db-spec
                             (sql/select * :users (sql/where {:email email})))]
    (first user-seq)))

(defn create
  [fname lname email encrypted-pass]
  (jdbc/insert! db-spec
      :users
      [:fname :lname :email :encrypted_password]
      [fname lname email encrypted-pass]))
