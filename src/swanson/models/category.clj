(ns swanson.models.category
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [java-jdbc.ddl :as ddl]
            [java-jdbc.sql :as sql]
            [swanson.models.matcher :as matcher]
            [swanson.utils :refer [db-config load-config date-converter]]
            [ragtime.jdbc :as migration-jdbc]
            [ragtime.repl :as repl])
  (:import [java.security MessageDigest]
           [javax.xml.bind DatatypeConverter]))

(def db-spec
  (db-config))

(defn create
  [category]
  (jdbc/insert! db-spec
      :categories
      [:name]
      [category]))

(defn by-name
  [name]
  (jdbc/query db-spec
    (sql/select * :categories (sql/where {:name name}))))

(defn category-id
  "Get id corresponding to category match or return 'unknown'"
  [category-name]
  (let [fetch-category
        (fn [name] (get (first (jdbc/query db-spec
                               (sql/select * :categories
                               (sql/where {:name name})))) :id))
        category-id (fetch-category category-name)]
    (or category-id (fetch-category "unknown"))))

(def id
  (memoize category-id))

(defn transactions
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

(defn monthly
  "monthly rollup of a category"
  [category-id]
  (jdbc/query db-spec
              ["SELECT EXTRACT(month FROM transactions.date) AS month,
               round(SUM(transactions.amount)::numeric, 2) AS amount
               FROM transactions WHERE category_id = ?
               GROUP by month ORDER
               BY month DESC" category-id]))

(defn daily-current-year
  "daily rollup of category for current year"
  [category-id]
  (jdbc/query db-spec
              ["SELECT EXTRACT(DOY from transactions.date) doy,
               round(SUM(transactions.amount)::numeric, 2) AS amount
               FROM transactions WHERE category_id = ?
               AND EXTRACT(ISOYEAR FROM transactions.date) =
                EXTRACT(ISOYEAR FROM current_date)
               GROUP by doy
               ORDER BY doy DESC" category-id]))

(defn ytd
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

(defn last-month
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

(defn average
  "average category transaction per month"
  [category-id]
  (jdbc/query db-spec
              ["SELECT
               EXTRACT(month from transactions.date) mon,
               EXTRACT(ISOYEAR from transactions.date) yr,
               ROUND(AVG(transactions.amount)::numeric, 2) average
               FROM transactions WHERE category_id = ?
               GROUP by mon, yr ORDER by mon desc" category-id]))

(defn all
  "list of all categories"
  []
  (jdbc/query db-spec
              ["SELECT id, name
               FROM categories ORDER BY name ASC"]))

(defn by-week [category-id]
  (jdbc/query db-spec ["SELECT date_trunc('week', t.date) AS Week,
                       round(SUM(t.amount)::numeric, 2) AS total
                       FROM transactions t
                       WHERE t.date > now() - interval '1 year'
                       AND t.category_id = ?
                       GROUP BY Week
                       ORDER BY Week ASC LIMIT 12" category-id]))
