(ns swanson.models.db
  (:require [clojure.java.jdbc :as jdbc]
            [java-jdbc.ddl :as ddl]
            [java-jdbc.sql :as sql]
            [swanson.utils :refer [date-converter]]
            [clj-time.core :as time-core])
  (:import [java.security MessageDigest]
           [javax.xml.bind DatatypeConverter]))

(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname "//localhost:5432/swanson"
              :user "admin"
              :password "admin"})

(defn create-users-table []
  (jdbc/db-do-commands db-spec
                       (ddl/create-table
                         :users
                         [:id "serial primary key"]
                         [:fname "varchar(100) NOT NULL"]
                         [:lname "varchar(100) NOT NULL"]
                         [:email "varchar(100) NOT NULL"]
                         [:encrypted_password "varchar(100)"])))

(defn create-transactions-table []
  (jdbc/db-do-commands db-spec
                       (ddl/create-table
                         :transactions
                         [:id "serial primary key"]
                         [:amount "float NOT NULL"]
                         [:date "date NOT NULL"]
                         [:category_id "int NOT NULL"]
                         [:description "varchar(512) NOT NULL"])))

(defn create-categories-table []
  (jdbc/db-do-commands db-spec
                       (ddl/create-table
                         :categories
                         [:id "serial primary key"]
                         [:name "varchar(512) NOT NULL"])))

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

(defn get-category
  [name]
  (jdbc/query db-spec
      (sql/select * :categories (sql/where {:name name}))))

(defn create-category
  [category]
  (jdbc/insert! db-spec
      :categories
      [:name]
      [category]))

(defn get-transaction
  [id]
  (jdbc/query db-spec
              (sql/select * :transactions (sql/where {:id id}))))

(def ^:dynamic *default-category-id* 1)

(defn create-transaction
  [{:keys [amount date description]}]
  (let [converted-date (date-converter date)]
    (jdbc/insert! db-spec
      :transactions
      [:amount :date :category_id :description]
      [amount converted-date *default-category-id* description])))

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
  (jdbc/query db-spec [week-grouping-query]))

(get-transactions-by-week)

(defn for-category
  [category-id]
  (jdbc/query db-spec
    ["SELECT id, date, amount, category_id, description
     FROM transactions
     WHERE category_id = ?
     ORDER BY date DESC" category-id]))

(defn update-category-id
  "Update category id"
  [id new-category-id]
  (jdbc/update! db-spec :transactions
                {:category_id new-category-id}
                (sql/where {:id id})))

(defn transaction-exists
  "checks if the transaction record already exists"
  [{:keys [amount date description]}]
  (let [result
        (jdbc/query db-spec
                    ["SELECT COUNT(*) FROM transactions WHERE
                     amount = ? AND
                     date = date(?) AND
                     description = ?" amount date description])]
    (> (:count (first result)) 0)))

(defn get-all-transactions
  [limit]
  (jdbc/query db-spec
              ["SELECT * FROM transactions ORDER by date DESC LIMIT ?" limit]))
