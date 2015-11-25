(ns swanson.models.db
  (:require [clojure.java.jdbc :as jdbc]
             [swanson.utils :refer [date-converter]])
  (:import [java.security MessageDigest]
           [javax.xml.bind DatatypeConverter]))

(def db {:subprotocol "postgresql"
         :subname "//localhost/swanson"
         :user "admin"
         :password "admin"})

(defmacro with-db [f & body]
  `(jdbc/with-connection ~db (~f ~@body)))

(defn create-users-table []
  (with-db jdbc/create-table
      :users
      [:id "serial primary key"]
      [:fname "varchar(100) NOT NULL"]
      [:lname "varchar(100) NOT NULL"]
      [:email "varchar(100) NOT NULL"]
      [:encrypted_password "varchar(100)"]))

(defn create-transactions-table []
  (with-db jdbc/create-table
      :transactions
      [:id "serial primary key"]
      [:amount "float NOT NULL"]
      [:date "date NOT NULL"]
      [:category_id "int NOT NULL"]
      [:description "varchar(512) NOT NULL"]))

(defn create-categories-table []
  (with-db jdbc/create-table
      :categories
      [:id "serial primary key"]
      [:name "varchar(512) NOT NULL"]))

(defn get-user
  [email]
  (with-db jdbc/with-query-results
    res ["SELECT * FROM users WHERE email = ?" email] (first res)))

(defn create-user
  [fname lname email encrypted-pass]
  (with-db jdbc/insert-values
      :users
      [:fname :lname :email :encrypted_password]
      [fname lname email encrypted-pass]))

(defn get-category
  [name]
  (with-db jdbc/with-query-results
      res ["SELECT * FROM categories WHERE name = ?" name] (first res)))

(defn create-category
  [category]
  (with-db jdbc/insert-values
      :categories
      [:name]
      [category]))

(defn get-transaction
  [id]
  (with-db jdbc/with-query-results
      res ["SELECT * FROM transactions WHERE id = ?" id] (first res)))

(defn create-transaction
  [{:keys [amount date description]}]
  (let [converted-date (date-converter date)]
    (with-db jdbc/insert-values
      :transactions
      [:amount :date :category_id :description]
      [amount converted-date 1 description])))

(defn- sha256-digest [bs]
  (doto (MessageDigest/getInstance "SHA-256") (.update bs)))

(defn sha256 [msg]
  (-> msg .getBytes sha256-digest .digest DatatypeConverter/printHexBinary))

(defn mk-transaction-id
  [date amount description]
  (sha256 (apply str date amount description)))

(def week-grouping-query
  "SELECT date_trunc('week', t.date) AS Week , SUM(t.amount) AS total
  FROM transactions t
  WHERE t.date > now() - interval '1 year'
  AND t.amount < 0.00
  GROUP BY Week
  ORDER BY Week")

(defn get-transactions-by-week []
  (with-db jdbc/with-query-results rows [week-grouping-query]
      (doall rows)))

(defn for-category
  [category-id]
  (with-db jdbc/with-query-results
    rows
    ["SELECT id, date, amount, category_id, description
     FROM transactions
     WHERE category_id = ?
     ORDER BY date DESC" category-id] (doall rows)))

(defn update-category-id
  "Update category id"
  [id new-category-id]
  (with-db jdbc/update-values
    :transactions
    ["id=?" id]
    {:category_id new-category-id}))
