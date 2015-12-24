(ns swanson.utils-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [swanson.utils :refer :all]
            [clj-time.core :as time-core]))

(def filepath (io/resource "test.csv"))

(deftest get-csv-test
  (testing "Test basic CSV functionality"
    (let [csv (swanson.utils/get-csv filepath)]
      (is (= (first csv) ["2014-07-26" "683734.5288" "" "" "fake description 1"]))
      (is (= (count csv) 2))
      (is (= (last csv) ["2014-07-27" "730393.8492" "" "" "fake description 2"])))))

(deftest parse-bank-csv-test
  (testing "test CSV to seq of maps functionality"
    (let [csv (swanson.utils/parse-bank-csv filepath)]
      (is (= (keys (first csv)) [:date :amount :description]))
      (is (= (vals (last csv)) ["2014-07-27" "730393.8492" "fake description 2"])))))

(deftest bank-date-to-date-test
  (testing "test that we can convert a date string to a date [for inserting]"
    (let [date "2015-11-14"]
      (is (= (bank-date-to-date date) (time-core/date-time 2015 11 14))))))

(deftest date-to-timestamp-test
  (testing "can convert a DateTime to a timestamp"
    (is (= (java.sql.Timestamp. 1447459200000) (date-to-timestamp (time-core/date-time 2015 11 14))))))

(deftest date-converter-test
  (testing "can convert a string to a timestamp"
    (is (= (java.sql.Timestamp. 1447459200000) (date-converter "2015-11-14")))))

(deftest transaction-parser-test
  (testing "can parse a valid transaction"
    (let [expected-map {:date "2015-11-19"
                        :description "fake description"
                        :amount 1.00}
          test-json-string "{\"date\": \"2015-11-19\", \"description\": \"fake description\", \"amount\": 1.00}"]
      (is (= expected-map (parse-transaction test-json-string))))))


(deftest parse-number-test
  (testing "can convert string to number (and take default)"
    (is (= 0 (parse-number "foo"))
    (is (= 1 (parse-number "1"))
    (is (= 10 (parse-number "foo" 10)))))))

(deftest json-response-test
  (testing "can build generic JSON endpoint response"
    (let [expected-response {:status 200,
                             :headers {"Content-Type" "application/json"},
                             :body "{\"baz\":[1,2,3],\"bar\":\"this is bar\",\"foo\":\"this is foo\"}"}]
      (is (= expected-response (json-response {:foo "this is foo"
                                              :bar "this is bar"
                                              :baz [1 2 3]}))))))


(deftest pad-days-pads-das-with-zero
  (testing "given a map of days, any missing days are set as zero"
    (let [missing-days [{1.0 100, 3.0 100}]]
      (is (= [{1.0 100, 2.0 0.0, 3.0 100}]
             (pad-days missing-days [:doy :sum]))))))
