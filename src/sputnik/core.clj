(ns sputnik.core
  (:use [clojure.java.io :only [reader writer]]
        [clojure.core.server :only [start-server]]
        [clojure.core.async
         :refer [>!! go chan <! go-loop]]
        [sputnik.utils :as utils])
  (:gen-class))

;; Objectives:
;; * Function to emit the periodic signal (done)
;; * Listen to on a TCP port for new periodic signal functions (done)
;; * Persist periodic signal function

(def default "(str \"Bip!\")")
(def ^:dynamic *signal-action*)

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
  (binding [*signal-action* default]
    (go-loop []
            (->>
             (<! shared-data)
             (set! *signal-action*))
            (recur))
    ;; FIXME go-loop
    ;; Exception in thread "async-dispatch-2" java.lang.IllegalStateException: Can't set!: *signal-action* from non-binding thread
    ;;     at clojure.lang.Var.set(Var.java:220)
    ;;     at sputnik.core$_main$fn__10676$state_machine__7878__auto____10679$fn__10681.invoke(core.clj:34)
    ;;     at sputnik.core$_main$fn__10676$state_machine__7878__auto____10679.invoke(core.clj:34)
    (utils/set-interval 5000
                        (->
                         *signal-action*
                         utils/println-evalued))))
