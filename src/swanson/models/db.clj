(ns swanson.models.db
  (:require [clojure.java.jdbc :as sql])
  (:import [java.security MessageDigest]
           [javax.xml.bind DatatypeConverter]))


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

(defn create-transactions-table []
  (sql/with-connection db
    (sql/create-table
      :transactions
      [:id "serial primary key"]
      [:amount "float NOT NULL"]
      [:date "date NOT NULL"]
      [:category_id "int NOT NULL"]
      [:description "varchar(512) NOT NULL"])))

(defn create-categories-table []
  (sql/with-connection db
    (sql/create-table
      :categories
      [:id "serial primary key"]
      [:name "varchar(512) NOT NULL"])))

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

(defn get-category
  [name]
  (sql/with-connection
    db
    (sql/with-query-results
      res ["SELECT * FROM categories WHERE name = ?" name] (first res))))

(defn make-category
  [category]
  (sql/with-connection
    db
    (sql/insert-values
      :categories
      [:name]
      [category])))

(defn get-transaction
  [id]
  (sql/with-connection
    db
    (sql/with-query-results
      res ["SELECT * FROM transactions WHERE id = ?" id] (first res))))

(defn make-transaction
  [amt dt desc category-id]
  (sql/with-connection
    db
    (sql/insert-values
      :transactions
      [:amount :date :category_id :description]
      [amt dt category-id desc])))

(defn- sha256-digest [bs]
  (doto (MessageDigest/getInstance "SHA-256") (.update bs)))

(defn sha256 [msg]
  (-> msg .getBytes sha256-digest .digest DatatypeConverter/printHexBinary))

(defn mk-transaction-id
  [date amount description]
  (sha256 (apply str date amount description)))
