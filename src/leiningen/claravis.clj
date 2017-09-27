(ns leiningen.claravis
  (:require [claravis.core :as cc]
            [clojure.java.io]
            [leiningen.core.project]
            [leiningen.core.eval]))

(def project-name-set '#{claravis/claravis})

(defn- project->claravis-dep [project]
  (first (filter #(and (vector? %)
                       (= 2 (count %))
                       (contains? project-name-set (first %))
                       (string? (second %)))
                 (tree-seq coll? seq project))))


(defn- merge-project-dep [project]
  (let [dep (project->claravis-dep project)]
    (leiningen.core.project/merge-profiles project [{:dependencies [dep]}])))


(defn claravis [project & args]
  (let [project (merge-project-dep project)]
    (leiningen.core.eval/eval-in-project project
                                         `(claravis.core/main ~@args)
                                         '(require 'claravis.core)))
  nil)
