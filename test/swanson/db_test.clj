(ns swanson.db-test
  (:require [clojure.test :refer :all]
            [swanson.models.db :refer :all]))

(deftest test-mk-transaction-id-encodes
  (testing "encodes transactions"
    (is (= (mk-transaction-id "2015-10-15" 21.0 "Monkeys")
           "35A463DBF9926D1BE1C354EF33B1D375047BF1BD6C2684808C8CA8A93E55F4B3"))))
