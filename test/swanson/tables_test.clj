(ns swanson.tables-test
  (:require [clojure.test :refer :all]
            [swanson.views.tables :refer :all]))

;; TODO the get syntax is ugly
(deftest test-category-form
  (testing "should return POST uri, select id, values, and names"
    (let [test-categories [["foo" 1] ["bar" 2] ["baz" 3]]
          test-transaction-id 4
          result (category-form test-categories test-transaction-id "http://example.com")
          form (get (get result 2) 1)
          select-metadata (get (get (get (get result 2) 2) 3) 1)
          options (get (get (get (get result 2) 2) 3) 2)]
    (is (= (:post form) "http://example.com"))
    (is (= (:role form) "form"))
    (is (= (:name select-metadata) "category-transaction-4"))
    (is (= '(1 2 3) (map #(:value (second %)) options)))
    (is (= '("foo" "bar" "baz") (map #(get % 2 ) options))))))
