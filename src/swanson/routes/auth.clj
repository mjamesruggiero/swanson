(ns swanson.routes.auth
  (:require [hiccup.form :refer :all]
            [compojure.core :refer :all]
            [swanson.routes.home :refer :all]
            [swanson.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as response]
            [noir.validation :as vali]))

(defn valid? [id pass pass1]
  (vali/rule (vali/has-value? id)
             [:id "user id required"])
  (vali/rule (vali/min-length? pass 5)
             [:pass "password must be at least 5 characters"])
  (vali/rule (= pass pass1)
             [:pass "entered passwords do not match"])
  (not (vali/errors? :id :pass :pass1)))

(defn error-item [[error]]
   [:div.error error])

(defn registration-page [& [id]]
  (layout/common
    (form-to [:post "/register"]
             (label "user-id" "user id")
             (text-field "id" id)
             [:br]
             (label "pass" "password")
             (password-field "pass")
             [:br]
             (label "pass1" "retype password")
             (password-field "pass1")
             [:br]
             (submit-button "create account"))))

(defn handle-registration [id pass pass1]
  (session/put! :user id)
  (response/redirect "/"))

(defroutes auth-routes
  (GET "/register" []
       (registration-page))
  (POST "/register" [id pass pass1]
        (handle-registration id pass pass1)))
