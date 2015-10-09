(ns swanson.models.db
  (:require [clojure.java.jdbc :as sql]))

(def db {:subprotocol "postgresql"
         :subname "//localhost/swanson"
         :user "admin"
         :password "admin"})

(defn create-users-table []
  (sql/with-connection db
    (sql/create-table
      :users
      [:id "varchar(32) PRIMARY KEY"]
      [:username "varchar(100)"]
      [:pass "varchar(100)"])))

(defn get-user "first query" [id]
  (sql/with-connection db
    (sql/with-query-results
      res ["SELECT * FROM users WHERE id = ?"] (first res))))
