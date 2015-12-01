(ns swanson.matcher-test
  (:require [clojure.test :refer :all]
            [swanson.models.matcher :refer :all]))

(deftest basic-match-test
  (testing "Matcher can match amazon"
    (let [description "CHECK CRD PURCHASE 10/12 AMAZON MKTPLACE PMTS AMZN.COM/BILL WA 286140004224448 01"]
      (is (= "amazon" (match-description description))))))
