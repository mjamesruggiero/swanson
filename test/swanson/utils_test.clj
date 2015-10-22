(ns swanson.utils-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [swanson.utils :refer :all]))

(def filepath (io/resource "test.csv"))

(deftest get-csv-test
  (testing "Test basic CSV functionality"
    (let [csv (swanson.utils/get-csv filepath)]
      (is (= (first csv) ["2014-07-26" "683734.5288" "" "" "fake description 1"]))
      (is (= (count csv) 2))
      (is (= (last csv) ["2014-07-27" "730393.8492" "" "" "fake description 2"])))))

(deftest parse-bank-csv
  (testing "test CSV to seq of maps functionality"
    (let [csv (swanson.utils/parse-bank-csv filepath)]
      (is (= (keys (first csv)) [:date :amount :description]))
      (is (= (vals (last csv)) ["2014-07-27" "730393.8492" "fake description 2"])))))
