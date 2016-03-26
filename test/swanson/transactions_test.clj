(ns swanson.transactions-test
  (:use clojure.test
        ring.mock.request
        swanson.handler))

(deftest test-transactions-route
  (testing  "transactions route works"
    (let  [response  (app  (request :get  "/transactions"))]
      (is  (= (:status response) 200)))))

(def put-args
  (array-map :description "another fake-ass decscription"
             :amount 100.00
             :category_id 4
             :date "2015-11-20"))

(def req
  (request :put "/transactions/826" put-args))

(deftest test-transactions-route
  (testing  "transactions route works"
    (let  [response (app req)]
      (is  (= (:status response) 201)))))
