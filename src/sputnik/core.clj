(ns sputnik.core
  (:use [clojure.java.io :only [reader writer]]
        [server.socket :only [create-server]]
        [clojure.core.async :as a :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout alt!]])
  (:gen-class))

;; Objectives:
;; * Function to emit the periodic signal
;; * Listen to on TCP port for new periodic signal functions
;; * Persist periodic signal function

(def default "(println \"Bop!\")")

(defn- signals-server [c]
  (fn [in out]
   (binding [*in* (reader in)
             *out* (writer out)
             *err* (writer System/err)]
     (print "\nType EDN: ") (flush)
     (loop [input (read-line)]
       (when input
         (>!! c input)
         (.flush *err*)
         (print "\nType EDN: ") (flush)
         (recur (read-line))))
     )))

(defn- set-interval [callback ms] 
  (future
    (while true
      (do (Thread/sleep ms)
          (try (callback)
               (catch Exception e (prn (str "caught exception: " (.getMessage e)))))))))

(defn- client-request [c]
  (fn []
    (go
     (println (eval
               (read-string
                (alt!
                 c ([v] v)
                 :default default)))))))

(defn -main
  [& args]
  (def shared-data (chan 128))
  (defonce server (create-server (Integer. 3333) (signals-server shared-data)))
  (def bip (set-interval #(println "Bip!") 5000))
  (future (do
            (Thread/sleep 30000)
            (println "Going to cancel previous periodic signal")
            (future-cancel bip)
            (println "Going to kick a new periodic signal")
            (set-interval (client-request shared-data) 5000))))
