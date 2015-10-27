(ns swanson.views.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer :all]
            [noir.session :as session]))

(defn base [& content]
  (html5
    [:head
     [:title "Welcome to swanson"]
     [:script {:type "text/javascript", :src "https://www.google.com/jsapi"}]
     [:script {:type "text/javascript", :src "//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"}]
     (include-css "/css/screen.css")]
    [:body content]))

(defn common [& content]
  (base
    (if-let [user (session/get :user)]
      [:div (link-to "/logout" (str "logout " user))]
      [:div (link-to "/register" "register")
        (form-to [:post "/login"]
                 (text-field {:placeholder "screen name"} "id")
                 (password-field {:placeholder "password"} "pass")
                 (submit-button "login"))])
    content))
