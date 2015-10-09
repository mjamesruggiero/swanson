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
      [:id "serial primary key"]
      [:fname "varchar(100) NOT NULL"]
      [:lname "varchar(100) NOT NULL"]
      [:email "varchar(100) NOT NULL"]
      [:encrypted_password "varchar(100)"])))

(defn get-user
  [id]
  (sql/with-connection
    db
    (sql/with-query-results
      res ["SELECT * FROM users WHERE id = ?" id] (first res))))

(defn make-user
  [fname lname email encrypted-pass]
  (sql/with-connection
    db
    (sql/insert-values
      :users
      [:fname :lname :email :encrypted_password]
      [fname lname email encrypted-pass])))
