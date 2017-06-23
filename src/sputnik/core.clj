(ns sputnik.core
  (:use [clojure.java.io :only [reader writer]]
        [clojure.core.server :only [start-server]]
        [clojure.core.async :as a :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout alt!]])
  (:gen-class))

;; Objectives:
;; * Function to emit the periodic signal (done)
;; * Listen to on TCP port for new periodic signal functions (done)
;; * Persist periodic signal function

(def default "(str \"Bip!\")")

(defn- signals-server [c]
  (print "\nType EDN: ") (flush)
  (loop [input (read-line)]
    (when input
      (>!! c input)
      (print "\nType EDN: ") (flush)
      (recur (read-line)))))

(defn- set-interval [callback ms] 
  (future
    (while true
      (do (Thread/sleep ms)
          (try (callback)
               (catch Exception e (prn
                                   (str
                                    "caught exception: "
                                    (.getMessage e)))))))))

(defn println-evalued
  [s]
  (-> s
      read-string
      eval
      println))

(defn- process [c]
  (fn []
    (go
     (println-evalued (alt!
                       c ([v] v)
                       :default default)))))

(defn -main
  [& args]
  (def shared-data (chan 128))
  (start-server {:port 3333
                 :name "signals-server"
                 :accept 'sputnik.core/signals-server
                 :args [shared-data]})
  (future
    (do
      (set-interval (process shared-data) 5000))))
