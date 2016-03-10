(ns swanson.views.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer :all]
            [swanson.utils :as utils]
            [noir.session :as session]))

(defn header []
  [:nav {:class "navbar navbar-default navbar-fixed-top" }
   [:a {:href "/" :class "navbar-brand"} "Swanson" ]
   [:a {:href "/summary" :class "navbar-brand"} "Summary"]
   (if-let [user (session/get :user)]
     [:div.pull-right (link-to "/logout" (str "logout " user))]
     [:div.pull-right (link-to "/register" "register")
      (form-to [:post "/login"]
               (text-field {:placeholder "email"} "email")
               (password-field {:placeholder "password"} "pass")
               (submit-button {:class "btn btn-default btn-xs"} "login"))])])

(defn panel-table [headline header-row rows]
  [:div {:class "panel panel-default"}
    [:div {:class "panel-heading"} headline]
    [:div {:class "panel-body"}
    [:table {:class "table"}
     [:tr (utils/map-tag :th header-row)]
     (for [row rows]
       [:tr (utils/map-tag :td row)])]]])

(defn base [& content]
  (html5
    [:head
     [:title "swanson"]
     [:script {:type "text/javascript", :src "https://www.google.com/jsapi"}]
     [:script {:type "text/javascript", :src "//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"}]
     [:script {:src "/js/bootstrap.min.js", :type "text/javascript"}]
     (include-css "/css/screen.css")
     (include-css "/css/bootstrap.min.css")
     (include-css "/css/bootstrap-theme.min.css")]
    [:body
     (header)
     [:div {:class "container"} content] ]))

(defn common [& content]
  (base content))

(defn four-oh-one
  []
  (common
    [:div {:class "container"}
    [:div {:class "container"} [:h2 "401 not authorized"]]]))
