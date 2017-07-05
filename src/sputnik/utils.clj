(ns sputnik.utils
  (:use  [clojure.core.async
          :refer [go alt!]])
  (:gen-class))

;; TODO Use to implement set-interval
;; (go-loop [seconds 1]
;;          (<! (timeout 1000))
;;          (print "waited" seconds "seconds")
;;          (recur (inc seconds)))

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

(defmacro read- [c]
  "Read from c"
  `(go
    (<!! ~c)))

(defn println-evalued
  [s]
  (-> s
      read-string
      eval
      println))
