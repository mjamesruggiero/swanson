(ns swanson.models.db
  (:require [clj-time.core :as time-core]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [java-jdbc.ddl :as ddl]
            [java-jdbc.sql :as sql]
            [swanson.models.matcher :as matcher]
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

(defn get-category-id
  "Get id corresponding to category match or return 'unknown'"
  [category-name]
  (let [fetch-category
        (fn [name] (get (first (jdbc/query db-spec
                               (sql/select * :categories
                               (sql/where {:name name})))) :id))
        category-id (fetch-category category-name)]
    (or category-id (fetch-category "unknown"))))

(defn create-transaction
  [{:keys [amount date description]}]
  (let [converted-date (date-converter date)
        matching-category (matcher/match-description description)
        category-id (get-category-id matching-category)]
    (jdbc/insert! db-spec
      :transactions
      [:amount :date :category_id :description]
      [amount converted-date category-id description])))

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
  GROUP BY Week
  ORDER BY Week DESC LIMIT 6")

(defn get-transactions-by-week []
  (jdbc/query db-spec [week-grouping-query]))

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
    (pos? (:count (first result)))))

(defn get-all-transactions
  [limit]
  (jdbc/query db-spec
              ["SELECT * FROM transactions ORDER by date DESC LIMIT ?" limit]))

(defn recent-transactions
  [limit]
  (jdbc/query db-spec
              ["SELECT t.id, t.amount, t.date, t.description, c.name
               FROM transactions t
               INNER JOIN categories c
               ON c.id = t.category_id
               ORDER BY t.date DESC LIMIT ?" limit]))

(defn category-monthly
  "monthly rollup of a category"
  [category-id]
  (jdbc/query db-spec
              ["SELECT EXTRACT(month FROM transactions.date) AS month,
               SUM(transactions.amount) AS amount
               FROM transactions WHERE category_id = ?
               GROUP by month ORDER
               BY month DESC" category-id]))

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

(defn category-daily-current-year
  "daily rollup of category for current year"
  [category-id]
  (jdbc/query db-spec
              ["SELECT EXTRACT(DOY from transactions.date) doy,
               SUM(transactions.amount)
               FROM transactions WHERE category_id = ?
               AND EXTRACT(ISOYEAR FROM transactions.date) =
                EXTRACT(ISOYEAR FROM current_date)
               GROUP by doy
               ORDER BY doy DESC" category-id]))

(defn categories-ytd
  "descending cost of categories for current year"
  []
  (jdbc/query db-spec
   ["SELECT round(sum(amount)::numeric, 2) AS cost,
    categories.name AS category
    FROM transactions
    INNER JOIN categories ON transactions.category_id = categories.id
    WHERE EXTRACT(ISOYEAR FROM transactions.date) =
      EXTRACT(ISOYEAR FROM current_date)
    GROUP BY name
    ORDER BY cost DESC"]))

(defn categories-last-month
  "categories for last month"
  []
  (jdbc/query db-spec
              ["SELECT categories.name AS category,
               round(SUM(amount)::numeric, 2) AS amount
               FROM transactions INNER JOIN categories
               ON categories.id = transactions.category_id
               WHERE date >= date_trunc('month', now()) - INTERVAL '1 month'
               AND date <= date_trunc('MONTH', now()) - INTERVAL '1 day'
               GROUP by category ORDER by amount DESC"]))

(defn category-average
  "average category transaction per month"
  [category-id]
  (jdbc/query db-spec
              ["SELECT
               EXTRACT(month from transactions.date) mon,
               EXTRACT(ISOYEAR from transactions.date) yr,
               ROUND(AVG(transactions.amount)::numeric, 2) average
               FROM transactions WHERE category_id = ?
               GROUP by mon, yr ORDER by mon desc" category-id]))

(defn all-categories
  "list of all categories"
  []
  (jdbc/query db-spec
              ["SELECT id, name
               FROM categories ORDER BY name ASC"]))
