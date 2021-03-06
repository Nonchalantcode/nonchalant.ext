(ns nonchalant.functions
  (:require [nonchalant.constants :refer :all])
  (:import (java.net URL)
           (java.io File FileOutputStream BufferedInputStream)))

(set! *warn-on-reflection* true)

(defn noop [])

(defn- open-stream ^BufferedInputStream [^String url headers]
  (try 
    (let [connection (-> url (URL.) (.openConnection))]
      (if (nil? headers)
        (noop)
        (doseq [[header header-value] headers]
          (.setRequestProperty connection header header-value)))
      (.connect connection)
      (-> connection (.getInputStream) (BufferedInputStream.)))
    (catch Exception err
      (println (.getMessage err)))))
      
(defn- read-stream [^java.io.BufferedInputStream stream size]
  (repeatedly (fn []
                (let [barr (byte-array size)]
                  (let [nread (.read stream barr 0 size)]
                    [nread barr])))))

(defn download [url & {:keys [dest size headers filename]
                       :or {dest (File. ^String downloads-folder)
                            headers nil
                            size 1024
                            filename (str (gensym))}}]
  (with-open [stream (open-stream url headers)
              file (FileOutputStream. (File. ^File dest ^String filename))]
    (doseq [chunk (take-while (fn [[bytes-read]] (pos? bytes-read))
                              (read-stream stream size))]
      (.write ^FileOutputStream file (second chunk) 0 (first chunk)))))
