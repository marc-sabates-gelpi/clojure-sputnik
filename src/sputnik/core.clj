(ns sputnik.core
  (:use [clojure.java.io :only [reader writer]]
        [clojure.core.server :only [start-server]]
        [clojure.core.async
         :refer [>!! go chan <!!]]
        [sputnik.utils :as utils])
  (:gen-class))

;; Objectives:
;; * Function to emit the periodic signal (done)
;; * Listen to on a TCP port for new periodic signal functions (done)
;; * Persist periodic signal function

(def default "(str \"Bip!\")")

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
  (utils/set-interval 5000
                      (->
                       (utils/read- shared-data default)
                       <!!
                       utils/println-evalued)))
