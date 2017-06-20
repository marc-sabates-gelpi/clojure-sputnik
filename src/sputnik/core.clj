(ns sputnik.core
  (:use [clojure.java.io :only [reader writer]]
        [server.socket :only [create-server]])
  (:gen-class))

;; Objectives:
;; * Function to emit the periodic signal
;; * Listen to on TCP port for new periodic signal functions
;; * Persist periodic signal function

(defn- signals-server [in out]
  (binding [*in* (reader in)
            *out* (writer out)
            *err* (writer System/err)]

    (print "\nType EDN: ") (flush)
    (loop [input (read-line)]
      (when input
        (println (eval (read-string input)))
        (.flush *err*)
        (print "\nType EDN: ") (flush)
        (recur (read-line))))
    ))

(defn set-interval [callback ms] 
  (future
    (while true
      (do (Thread/sleep ms)
          (try (callback)
               (catch Exception e (prn (str "caught exception: " (.getMessage e)))))))))

(defn -main
  [& args]
  (defonce server (create-server (Integer. 3333) signals-server))
  (def bip (set-interval #(println "Bip!") 5000))
  (println "First action after setting the periodic signal")
  (future (do
            (Thread/sleep 30000)
            (println "Going to cancel previous periodic signal")
            (future-cancel bip)
            (println "Going to kick a new periodic signal")
            (set-interval #(println "Bop!") 5000))))

