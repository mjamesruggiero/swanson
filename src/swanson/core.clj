(ns swanson.core
  (:require
    [swanson.sentences  :refer [strings->sentences]]
    [swanson.charset    :refer [wrap-charset]]
    [swanson.translate  :refer [translate]]
    [swanson.session    :refer [new-session get-session]]
    [clojure.edn        :as edn]
    [compojure.core     :refer :all]
    [compojure.handler  :refer [api]]
    [ring.adapter.jetty :refer [run-jetty]]
    [ring.util.response :refer [response status]]
    [clj-http.client    :as client]))

(defn create-session []
  (let [snippets (repeatedly promise)
        translations (delay (map translate
                            (strings->sentences (map deref snippets))))]
    (new-session {:snippets snippets :translations translations})))

(defn accept-snippet [session n text]
  (deliver (nth (:snippets session) n) text))

(defn get-translation [session n]
  @(nth @(:translations session) n))

(defroutes app-routes
  (POST "/session/create" []
        (response (str (create-session))))

  (context "/session/:session-id" [session-id]
           (let [session (get-session (edn/read-string session-id))]
             (routes
               (PUT "/snippet/:n" [n :as {:keys [body]}]
                    (accept-snippet session (edn/read-string n) (slurp body))
                    (response "OK"))

               (GET "/translation/:n" [n]
                    (response (get-translation session (edn/read-string n))))))))


(defn -main [& args]
  (run-jetty (wrap-charset (api app-routes)) {:port 3000}))
