(ns sputnik.utils
  (:use  [clojure.core.async
          :refer [go alt!]])
  (:gen-class))

(defmacro set-interval [interval & body]
  "Execute the body forms every interval ms"
  `(future
    (while true
      (do 
          (try ~@body
               (catch Exception e# (prn
                                    (str
                                     "caught exception: "
                                     (.getMessage e#)))))
          (Thread/sleep ~interval)))))

(defmacro read- [c default]
  "Read from c with default"
  `(go
    (alt!
     ~c ([v#] v#)
     :default ~default)))

(defn println-evalued
  [s]
  (-> s
      read-string
      eval
      println))
