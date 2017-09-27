(ns leiningen.claravis
  (:require [claravis.core :as cc]
            [clojure.java.io]
            [leiningen.core.project]
            [leiningen.core.eval]))

(def project-name-set '#{claravis/claravis})

(defn- project->claravis-dep
  "Finds exact dependency version in project map. Returns [name version] tuple."
  [project]
  (first (filter #(and (vector? %)
                       (= 2 (count %))
                       (contains? project-name-set (first %))
                       (string? (second %)))
                 (tree-seq coll? seq project))))


(defn- merge-project-dep
  "Adds current plugin to dependencies map too."
  [project]
  (let [dep (project->claravis-dep project)]
    (leiningen.core.project/merge-profiles project [{:dependencies [dep]}])))


(defn claravis
  "Plugin entry point. Calls claravis.core/main function with args."
  [project & args]
  (let [project (merge-project-dep project)]
    (leiningen.core.eval/eval-in-project project
                                         `(claravis.core/main ~@args)
                                         '(require 'claravis.core))))
