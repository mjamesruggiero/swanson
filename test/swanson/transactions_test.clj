(ns swanson.transactions-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [swanson.handler :refer :all]))

(deftest test-transactions-vanilla
  (testing  "transactions route works"
    (let  [response  (app  (request :get  "/transactions"))]
      (is  (= (:status response) 200)))))

(def transaction-put-json "{\"category_id\": \"4\"}")

(def transaction-put "/transactions/826")

;;
;; TODO turn these back into happy path tests
;;
(deftest test-transactions-post
  (testing  "transactions route doesn't work"
    (let  [req (request :put transaction-put)
           response (app (body req transaction-put-json))]
      (is  (= (:status response) 403)))))

(deftest test-transactions-post-returns-forgery-error
  (testing  "transactions error is anti-forgery"
    (let  [req (request :put transaction-put)
           response (app (body req transaction-put-json))]
      (is  (= (:body response) "<h1>Invalid anti-forgery token</h1>")))))
