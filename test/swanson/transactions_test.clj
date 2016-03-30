(ns swanson.transactions-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [swanson.handler :refer :all]))

(deftest test-transactions-route
  (testing  "transactions route works"
    (let  [response  (app  (request :get  "/transactions"))]
      (is  (= (:status response) 200)))))

(deftest test-transactions-route
  (testing  "transactions route works"
    (let  [req (request :put "/transactions/826")
           json "{\"category_id\": \"4\"}"
           response (app (body req json))]
      (is  (= (:status response) 201)))))
