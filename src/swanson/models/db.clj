(ns swanson.models.db
  (:require [clojure.java.jdbc :as sql])
  (:import [java.security MessageDigest]
           [javax.xml.bind DatatypeConverter]))

(def db {:subprotocol "postgresql"
         :subname "//localhost/swanson"
         :user "admin"
         :password "admin"})

(defmacro with-db [f & body]
  `(sql/with-connection ~db (~f ~@body)))

(defn create-users-table []
  (with-db sql/create-table
      :users
      [:id "serial primary key"]
      [:fname "varchar(100) NOT NULL"]
      [:lname "varchar(100) NOT NULL"]
      [:email "varchar(100) NOT NULL"]
      [:encrypted_password "varchar(100)"]))

(defn create-transactions-table []
  (with-db sql/create-table
      :transactions
      [:id "serial primary key"]
      [:amount "float NOT NULL"]
      [:date "date NOT NULL"]
      [:category_id "int NOT NULL"]
      [:description "varchar(512) NOT NULL"]))

(defn create-categories-table []
  (with-db sql/create-table
      :categories
      [:id "serial primary key"]
      [:name "varchar(512) NOT NULL"]))

(defn get-user
  [email]
  (with-db sql/with-query-results
    res ["SELECT * FROM users WHERE email = ?" email] (first res)))

(defn create-user
  [fname lname email encrypted-pass]
  (with-db sql/insert-values
      :users
      [:fname :lname :email :encrypted_password]
      [fname lname email encrypted-pass]))

(defn get-category
  [name]
  (with-db sql/with-query-results
      res ["SELECT * FROM categories WHERE name = ?" name] (first res)))

(defn create-category
  [category]
  (with-db sql/insert-values
      :categories
      [:name]
      [category]))

(defn get-transaction
  [id]
  (with-db sql/with-query-results
      res ["SELECT * FROM transactions WHERE id = ?" id] (first res)))

(defn create-transaction
  [amt dt desc category-id]
  (with-db sql/insert-values
      :transactions
      [:amount :date :category_id :description]
      [amt dt category-id desc]))

(defn- sha256-digest [bs]
  (doto (MessageDigest/getInstance "SHA-256") (.update bs)))

(defn sha256 [msg]
  (-> msg .getBytes sha256-digest .digest DatatypeConverter/printHexBinary))

(defn mk-transaction-id
  [date amount description]
  (sha256 (apply str date amount description)))
