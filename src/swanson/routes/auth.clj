(ns swanson.routes.auth
  (:require [hiccup.form :refer :all]
            [compojure.core :refer :all]
            [swanson.routes.home :refer :all]
            [swanson.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [swanson.models.db :as db]
            [noir.util.crypt :as crypt]))

(defn valid? [fname lname email pass pass1]
  (vali/rule (vali/has-value? fname)
             [:fname "first name required"])
  (vali/rule (vali/has-value? lname)
             [:lname "last name required"])
  (vali/rule (vali/has-value? email)
             [:email "email required"])
  (vali/rule (vali/min-length? pass 5)
             [:pass "password must be at least 5 characters"])
  (vali/rule (= pass pass1)
             [:pass "entered passwords do not match"])
  (not (vali/errors? :id :pass :pass1)))

(defn error-item [[error]]
   [:div.error error])

(defn control [id label field]
  (list (vali/on-error id error-item)
  label field
  [:br]))

(defn registration-page [& [id]]
  (layout/common
    (form-to [:post "/register"]
             (control :fname
                      (label "fname" "first name")
                      (text-field {:tabindex 1} "fname"))
             (control :lname
                      (label "lname" "last name")
                      (text-field {:tabindex 1} "lname"))
             (control :email
                      (label "email" "email")
                      (text-field {:tabindex 1} "email"))
             (control :pass
                      (label "pass" "password")
                      (password-field {:tabindex 2} "pass"))
             (control :pass1
                      (label "pass1" "retype password")
                      (password-field {:tabindex 3} "pass1"))
             (submit-button {:tabindex 4} "create account"))))

(defn format-error [email ex]
  (cond
    (and (instance? org.postgresql.util.PSQLException ex)
         (= 0 (.getErrorCode ex)))
    (str "The user with email " email " already exists!")
    :else
    (str "An error (" ex ") has occurred while processing the request")))

(defn handle-registration [fname lname email pass pass1]
  (if (valid? fname lname email pass pass1)
    (try
      (db/create-user fname lname email (crypt/encrypt pass))
      (session/put! :user email)
      (resp/redirect "/")
      (catch Exception ex
        (vali/rule false [:email (format-error email ex)])
        (registration-page)))
    (registration-page email)))

(defn handle-login [email pass]
  (let [user (db/get-user email)]
    (if (and user (crypt/compare pass (:encrypted_password user)))
      (session/put! :user email)))
  (resp/redirect "/"))

(defn handle-logout []
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
  (GET "/register" []
       (registration-page))

  (POST "/register" [fname lname email pass pass1]
        (handle-registration fname lname email pass pass1))

  (POST "/login" [email pass]
        (handle-login email pass))

  (GET "/logout" []
       (handle-logout)))
