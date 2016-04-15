(ns swanson.models.db
  (:require [clj-time.core :as time-core]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [java-jdbc.ddl :as ddl]
            [java-jdbc.sql :as sql]
            [swanson.models.matcher :as matcher]
            [swanson.models.category :as category]
            [swanson.utils :refer [load-config date-converter]]
            [ragtime.jdbc :as migration-jdbc]
            [ragtime.repl :as repl])
  (:import [java.security MessageDigest]
           [javax.xml.bind DatatypeConverter]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;          config
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def config-file-name
  (or  (System/getenv  "SWANSON_CONFIG")
      "dev-config.edn"))

(def db-config
 (load-config
  (io/resource config-file-name)))

(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname (:subname db-config)
              :user (:user db-config)
              :password (:password db-config)})

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

(defn get-transaction
  [id]
  (jdbc/query db-spec
              (sql/select * :transactions (sql/where {:id id}))))


(defn create-transaction
  [{:keys [amount date description]}]
  (let [converted-date (date-converter date)
        matching-category (matcher/match-description description)
        c (category/id matching-category)]
    (jdbc/insert! db-spec
      :transactions
      [:amount :date :category_id :description]
      [amount converted-date c description])))

(def week-grouping-query
  "SELECT date_trunc('week', t.date) AS Week,
  round(SUM(t.amount)::numeric, 2) AS total
  FROM transactions t
  WHERE t.date > now() - interval '1 year'
  GROUP BY Week
  ORDER BY Week ASC LIMIT 12")

(defn get-transactions-by-week []
  (jdbc/query db-spec [week-grouping-query]))

(defn transaction-exists
  "checks if the transaction record already exists"
  [{:keys [amount date description]}]
  (let [result
        (jdbc/query db-spec
                    ["SELECT COUNT(*) FROM transactions WHERE
                     amount = ? AND
                     date = date(?) AND
                     description = ?" amount date description])]
    (pos? (:count (first result)))))

(defn get-all-transactions
  [limit]
  (jdbc/query db-spec
              ["SELECT * FROM transactions ORDER by date DESC LIMIT ?" limit]))

(defn recent-transactions
  [limit]
  (jdbc/query db-spec
              ["SELECT t.id,
               round(t.amount::numeric, 2) AS amount,
               t.date, t.description, c.name
               FROM transactions t
               INNER JOIN categories c
               ON c.id = t.category_id
               ORDER BY t.date DESC LIMIT ?" limit]))

(defn last-n-months
  "last n months of spend"
  [months]
  (jdbc/query db-spec
              ["SELECT EXTRACT(year FROM transactions.date) AS year,
               EXTRACT(month FROM transactions.date) AS month,
               round(SUM(transactions.amount)::numeric, 2) AS amount
               FROM transactions
               GROUP by year, month ORDER
               BY month DESC LIMIT ?" months]))
