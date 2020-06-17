(defproject claravis "0.1.0-SNAPSHOT"
  :description "Leiningen plugin for drawing Clara rule flow graphs"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [macroz/tangle "0.2.0"]]
  :eval-in-leiningen true
  :profiles {:dev {:dependencies [[com.cerner/clara-rules "0.16.0"]]
                   :plugins      [[com.jakemccrary/lein-test-refresh "0.21.1"]]}})
