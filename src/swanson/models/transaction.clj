(ns swanson.models.transaction
  (:require [clj-time.core :as time-core]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [java-jdbc.ddl :as ddl]
            [java-jdbc.sql :as sql]
            [swanson.models.matcher :as matcher]
            [swanson.models.category :as category]
            [swanson.utils :refer [db-config
                                   load-config
                                   date-converter
                                   replace-template]])
  (:import [java.security MessageDigest]
           [javax.xml.bind DatatypeConverter]))

(def db-spec
  (db-config))

(defn by-id
  [id]
  (jdbc/query db-spec
              (sql/select * :transactions (sql/where {:id id}))))

(defn- create-record
  "Inserts database row"
  [{:keys [amount date description]}]
  (let [converted-date (date-converter date)
        matching-category (matcher/match-description description)
        c (category/id matching-category)]
    (jdbc/insert! db-spec
                  :transactions
                  [:amount :date :category_id :description]
                  [amount converted-date c description])))

(defn exists
  "checks if the transaction record already exists"
  [{:keys [amount date description]}]
  (let [result
        (jdbc/query db-spec
                    ["SELECT COUNT(*) FROM transactions WHERE
                     amount = ? AND
                     date = date(?) AND
                     description = ?" amount date description])]
    (pos? (:count (first result)))))

(defn create
  "checks for existing record and inserts"
  [transaction]
  (when-not (exists transaction) (create-record transaction)))

(def week-grouping-query
  "SELECT date_trunc('week', t.date) AS Week,
  round(SUM(t.amount)::numeric, 2) AS total
  FROM transactions t
  WHERE t.date > now() - interval '1 year'
  GROUP BY Week
  ORDER BY Week ASC LIMIT 12")

(defn by-week []
  (jdbc/query db-spec [week-grouping-query]))

(defn all
  [limit]
  (jdbc/query db-spec
              ["SELECT * FROM transactions ORDER by date DESC LIMIT ?" limit]))

(defn recent
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

(defn spend-per-day
  "for date span of start to end,
  spend per day; days without spend appear with zero values"
  [start end]
  (let [template
        "WITH days AS
        (  SELECT day::date FROM
           GENERATE_SERIES(date '{start}', date '{end}', INTERVAL '1 day' day) day
        )
        SELECT
          COALESCE(SUM(t.amount), 0) AS daily_amount,
          d.day AS date
        FROM
          days d LEFT JOIN transactions t ON t.date = d.day
        GROUP BY d.day
        ORDER BY d.day"
        query (replace-template template {:start start :end end})]
    (jdbc/query db-spec [query])))
