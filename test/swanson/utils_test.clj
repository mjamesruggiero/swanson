(ns swanson.utils-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [swanson.utils :refer :all]))

(def filepath (io/resource "test.csv"))

(deftest get-csv-test
  (testing "Test basic CSV functionality"
    (let [csv (swanson.utils/get-csv filepath)]
      (is (= (first csv) ["date" "total_spend"]))
      (is (= (count csv) 3))
      (is (= (last csv) ["2014-07-27" "730393.8492"])))))
