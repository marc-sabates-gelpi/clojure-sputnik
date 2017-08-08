(ns sputnik.core
  (:use [clojure.java.io :only [reader writer]]
        [clojure.core.server :only [start-server]]
        [clojure.core.async
         :refer [>!! chan <! go-loop]]
        [utils.core :as utils])
  (:gen-class))

;; Objectives:
;; * Function to emit the periodic signal (done)
;; * Listen to on a TCP port for new periodic signal functions (done)

(def signal-action (agent "(str \"Bip!\")"))

(defn- signals-server [c]
  (print "\nType EDN: ") (flush)
  (loop [input (read-line)]
    (when input
      (>!! c input)
      (print "\nType EDN: ") (flush)
      (recur (read-line)))))

(defn -main
  [& args]
  (println "~~ System started ~~")
  (def shared-data (chan 128))
  (start-server {:port 3333
                 :name "signals-server"
                 :accept `signals-server
                 :args [shared-data]})
  (go-loop []
           (->>
            (<! shared-data)
            (send signal-action utils/update-agent))
           (recur))
  (utils/set-interval 5000
                      (doto
                       @signal-action
                       utils/println-evalued))
  (utils/keep-running))
