(ns swanson.tables-test
  (:require [clojure.test :refer :all]
            [swanson.views.tables :refer :all]))

(deftest test-category-form
  (testing "should return POST uri, select id, values, and names"
    (let [test-categories [{:name "foo" :id 1}
                           {:name "bar" :id 2}
                           {:name "baz" :id 3}]
          test-transaction-id 4
          result (category-form test-categories test-transaction-id
                                "http://example.com")
          form (nth result 1)
          select-metadata (nth (nth result 2) 1)
          options (map second (nth (nth result 2) 2))
          names (map last (nth (nth result 2) 2))]
    (is (= (:post form) "http://example.com"))
    (is (= (:role form) "form"))
    (is (= (:name select-metadata) "category-transaction-4"))
    (is (= '(1 2 3) (map :value options)))
    (is (= '("foo" "bar" "baz") names)))))
