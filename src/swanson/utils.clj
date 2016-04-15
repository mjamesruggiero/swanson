(ns swanson.utils
  (:require
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [clj-time.format :as format]
    [clj-time.core :as time-core]
    [cheshire.core :refer :all]
    [clojure.edn :as edn]
    [clojure.data.json :as json]))

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
    (map row->map parsed-csv)))

(def bank-formatter
  (format/formatter "yyyy-MM-dd"))

(defn bank-date-to-date
  "convert standard date string to timestamp"
  [date-string]
  (format/parse bank-formatter date-string))

(defn date-to-timestamp
  [dt]
  (java.sql.Timestamp. (.getMillis dt)))

(defn date-converter
  "Syntactic sugar for string-to-to timestamp conversion"
  [datestring]
  (->> datestring
       bank-date-to-date
       date-to-timestamp))

(defn parse-transaction
  [json-str]
  (parse-string json-str true))

(defn parse-number
  "Reads number from string;
  if not a number, returns default or zero"
  [s & default]
  (if (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)
    (first (or default (list 0)))))

(extend-type java.sql.Date
  json/JSONWriter
  (-write [date out]
    (json/-write (str date) out)))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (generate-string data {:date-format "yyyy-MM-dd"})})

(defn pad-keys [numeric-map]
  (let [indices (keys numeric-map)
        start (reduce min indices)
        end (reduce max indices)
        mapped-zeros (into {}
                           (map hash-map
                                (range start end)
                                (repeat end 0)))]
    (merge mapped-zeros numeric-map)))

(defn map-tag
  "Simple helper for seqs we'll convert to Hiccup"
  [tag xs]
  (map (fn [x] [tag x]) xs))

(defn- get-delta-for-similar-maps
  [list-of-maps delta-key]
  (let [delta (apply - (map delta-key list-of-maps))]
    (merge (dissoc (first list-of-maps) delta-key) {:delta delta})))

(defn compare-category-tallies
  "compare changes in values of lists of maps"
  [list-of-maps-1 list-of-maps-2 delta-key grouping-key]
  (let [test-list-of-maps (into list-of-maps-1 list-of-maps-2)
        grouped (group-by grouping-key test-list-of-maps)]
    (map #(get-delta-for-similar-maps % delta-key) (vals grouped))))

(defn maps->keyed-seq
  "take a key from a list of maps
  and make it the one key that references
  each map entry's vals"
  [l k]
  (map #(assoc {} (k %) (vals %) ) l))

(defn maps->tablerows
  "converts seq of maps into header row
  and ordered seq of seqs"
  [ordered rows]
  (let [f (fn [m] (map m ordered))
        table (map f rows)]
    (cons ordered table)))

(defn load-config
  "loads EDN config from a file"
  [filename]
  (edn/read-string (slurp filename)))

(defn db-config
  "load db config"
  []
  (let [config-file-name (or (System/getenv "SWANSON_CONFIG") "dev-config.edn")
        conf (load-config (io/resource config-file-name))]
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname (:subname conf)
     :user (:user conf)
     :password (:password conf)}))
