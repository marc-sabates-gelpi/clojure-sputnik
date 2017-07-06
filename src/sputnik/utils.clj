(ns sputnik.utils
  (:use [clojure.core.async
         :refer [<! <!! timeout go go-loop]])
  (:gen-class))

(defmacro set-interval [interval & body]
  "Execute the body forms every interval ms"
  `(go-loop []
            (<! (timeout ~interval))
            (try ~@body
                 (catch Exception e# (prn
                                      (str
                                       "caught exception: "
                                       (.getMessage e#)))))
            (recur)))

(defmacro read- [c]
  "Read from c"
  `(go
    (<! ~c)))

(defn println-evalued
  [s]
  (-> s
      read-string
      eval
      println))

(defn update-agent [old new] new)

(defn keep-running
  "Creates a future that will block forever thus not letting the app calling it ever finish"
  []
  (future (<!! (timeout -1))))
