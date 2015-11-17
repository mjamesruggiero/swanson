(ns swanson.utils
  (:require
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [clj-time.format :as format]
    [clj-time.core :as time-core]))

; TODO might be better to do this w/o filehandle hassles
; a la http://stackoverflow.com/a/19656800
(defn get-csv
  [filepath]
  (with-open [in-file (io/reader filepath)]
    (doall
      (csv/read-csv in-file))))

(defn- row->map [row]
  (let [[date amount _ _ description] row]
    {:date date :amount amount :description description}))

(defn parse-bank-csv
  "parse csv into importable map"
  [filepath]
  (let [parsed-csv (get-csv filepath)]
    (map #(row->map %) parsed-csv)))

(def bank-formatter
  (format/formatter "yyyy-MM-dd"))

(defn bank-date-to-date
  "convert standard date string to timestamp"
  [date-string]
  (format/parse bank-formatter date-string))

(defn date-to-timestamp
  [dt]
  (java.sql.Timestamp. (.getMillis dt)))
