(ns swanson.utils
  (:require
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]))

(defn get-csv
  [filepath]
  (with-open [in-file (io/reader filepath)]
    (doall
      (csv/read-csv in-file))))
