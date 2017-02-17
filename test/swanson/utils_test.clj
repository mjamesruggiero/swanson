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
                             :body
                             "{\"foo\":\"this is foo\",\"bar\":\"this is bar\",\"baz\":[1,2,3]}"}]
      (is (= expected-response (json-response {:foo "this is foo"
                                               :bar "this is bar"
                                               :baz [1 2 3]}))))))

(deftest pad-days-pads-das-with-zero
  (testing "given a map of days, any missing days are set as zero"
    (let [missing-days {1.0 100, 3.0 100}]
      (is (= {2.0 0, 1.0 100, 3.0 100}
             (pad-keys missing-days))))))

(deftest map-tag-builds-hiccup-sequences
  (testing "given seq, produces tagged pairs"
    (let [test-seq ["foo" "bar" "baz" "quux"]
          expected [[:td "foo"] [:td "bar"] [:td "baz"] [:td "quux"]]]
      (is (= expected (map-tag :td test-seq))))))

(deftest delta-categories-returns-zero-for-no-change
  (testing "comparing two lists of maps of category amounts details percentage changes"
    (let [first-lom [{:category "unknown", :cost 2762.27M}
                     {:category "check", :cost 953.31M}
                     {:category "savings", :cost 801.00M}]
          second-lom [{:category "unknown", :cost 2762.27M}
                      {:category "check", :cost 953.31M}
                      {:category "savings", :cost 801.00M}]
          result (compare-category-tallies first-lom second-lom :cost :category)
          expected '({:category "unknown", :delta 0.00M}
                     {:category "check", :delta 0.00M}
                     {:category "savings", :delta 0.00M})]
      (is (= expected (compare-category-tallies first-lom second-lom :cost :category))))))

(deftest maps->keyed-seq-test
  (testing "turns seq of maps into keyed vectors of map values
           whose key is the identifying value (e.g. the 'id')"
    (let [m [{:foo "foo" :bar "bar"} {:foo "not-foo" :bar "not-bar"}]
          result (maps->keyed-seq m :foo)
          expected '({"foo" ("foo" "bar")} {"not-foo" ("not-foo" "not-bar")})]
      (is (= expected result)))))

(deftest maps->tablerows-test
  (testing "converts seq of maps into header row and ordered seq of seqs"
    (let [rows [{:foo "foo1" :bar "bar1" :baz "baz1"}
                {:foo "foo2" :bar "bar2" :baz "baz2"}]
          expected '([:bar :foo :baz]
                     ("bar1" "foo1" "baz1")
                     ("bar2" "foo2" "baz2"))]
      (is (= expected (maps->tablerows [:bar :foo :baz] rows))))))

(def config-filepath
  (io/resource "test-config.edn"))

(deftest load-config-test
  (testing "reads config file and gets appropriate key/vals"
    (let [conf (load-config config-filepath)]
      (is (= {:foo "foo-value" :bar "bar-value"} conf)))))

(deftest replace-template-test
  (testing "templates with all keys are correctly populated"
    (let [template "{foo} likes the {bar} in {baz}-time."
          m {:foo "Ted" :baz "spring" :bar "rain"}
          result (replace-template template m)]
      (is (= "Ted likes the rain in spring-time." result)))))

(deftest replace-template-test-fails-gracefully
  (testing "when keys are missing, evaluates to nil"
    (let [template "{foo} likes the {bar} in {baz}-time."
          m {:foo "Ted" :baz "spring"}]
      (is (nil? (replace-template template m))))))
