(ns nonchalant.ext
  (:require [cheshire.core :as JSON]
            (ring.adapter [jetty :refer :all])
            (ring.middleware [resource :refer :all]
                             [params :refer (params-request)])
            (ring.util [response :as response])
            (ring.mock [request :as r :refer :all])
            (compojure [core :refer :all])
            (nonchalant [functions :refer (noop download)])))

(set! *warn-on-reflection* true)

(defn with-content-type [response mime-type]
  (response/header response "Content-Type" mime-type))

(defn cors [request]
  (-> request 
   (response/header "Access-Control-Allow-Origin" "*")
   (response/header "Access-Control-Allow-Headers" "Origin, X-Requested-With, Content-Type, Accept")))

(defroutes routes-table
  (GET "/" req
       (-> (response/response "Server running\n")
           (response/header "X-Server-Says" "Beep")))
  (POST "/download" req
        (try 
          (let [{:strs [url headers filename]} (-> (:body req)
                                                   (java.io.InputStreamReader.)
                                                   (JSON/parse-stream))]
            (download url :headers headers)
            (response/response "Downloaded"))
            (catch Exception err
              (println (.getMessage err))))))
  
(def app (fn [request-map]
           (-> request-map params-request routes-table cors)))

(def server (run-jetty #'app {:join? false, :port 8000}))
