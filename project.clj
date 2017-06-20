(defproject sputnik "0.1.0-SNAPSHOT"
  :description "Excersice to practise (eval), opening ports and Extensible data notation"
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License (GPL) version 3"
            :url "https://www.gnu.org/licenses/gpl.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [server-socket "1.0.0"]]
  :main ^:skip-aot sputnik.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
