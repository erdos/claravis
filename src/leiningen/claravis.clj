(ns leiningen.claravis
  (:require [claravis.core :as cc]
            [clojure.java.io]))

(def claravis-profile '{:dependencies [[claravis "0.1.0-SNAPSHOT"]]})

(defn claravis [project ns-name file-name]
  (let [project (leiningen.core.project/merge-profiles project [claravis-profile])]
    (leiningen.core.eval/eval-in-project project
                                         `(do
                                            (require '~(symbol ns-name))
                                            (claravis.core/render-to-image-file
                                            (the-ns (symbol ~ns-name))
                                            (clojure.java.io/file ~file-name)))
                                         '(require 'claravis.core)))
  nil)
