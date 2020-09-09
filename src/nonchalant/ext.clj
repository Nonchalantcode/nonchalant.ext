(ns nonchalant.ext
  (:require [clj-http.client :as client])
  (:import (java.net URL)
           (java.io File FileOutputStream BufferedInputStream)))

(set! *warn-on-reflection* true)

(def user-home (. System getProperty "user.home"))
(def separator (File/separator))
(def downloads-folder (str user-home separator "Downloads"))

(defn read-stream [^java.io.BufferedInputStream stream size]
  (repeatedly (fn []
                (let [barr (byte-array size)]
                  (let [nread (.read stream barr 0 size)]
                    [nread barr])))))

(defn download
  ([url] (download url (File. ^String downloads-folder)))
  ([url ^java.io.File dest-folder]
   (with-open [stream (-> url URL. .openStream BufferedInputStream.)
              file (-> (File. dest-folder (str (gensym))) (FileOutputStream.))]
    (doseq [chunk (take-while (fn [[bytes-read]] (pos? bytes-read))
                              (read-stream stream 1024))]
      (.write ^FileOutputStream file (second chunk) 0 (first chunk))))))
