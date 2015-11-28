#!/bin/bash

curl -i \
-H "Accept: application/json" \
-H "Content-Type:application/json" \
-X PUT --data '{ "description": "another fake-ass decscription", "amount": 100.00, "category_id": 4, "date": "2015-11-20" }' \
"http://localhost:8080/transaction/826/category"
