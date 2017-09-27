(ns claravis.core
  (:require [tangle.core :as tangle]
            [clojure.java.io]))

(defn collect-rule-vars
  "Collects all vars in namespace that are clara rules"
  [the-ns] (doall (filter (comp true? :rule meta) (vals (ns-publics the-ns)))))

(defn- lhs->types [lhs]
  (doall (keep (some-fn :type (comp :type :from)) lhs)))

(defn collect-query-vars
  "Collects all vars in namespace that are clara queries."
  [the-ns] (doall (filter (comp true? :query meta) (vals (ns-publics the-ns)))))

(defn- all-symbols [expr]
  (->> expr
       (tree-seq #(and (coll? %) (not (and (list? %) (= 'quote (first %))))) seq)
       (filter symbol?)))

(defn ctor [sym]
  (assert (symbol? sym))
  (let [n (name sym)
        m (cond (.startsWith n "->")    (.substring n 2)
                (.startsWith n "map->") (.substring n 5)
                (.endsWith n ".")       (.substring n 0 (dec (.length n))))]
    (some-> m symbol)))

(defn analyze-rule [rule-var]
  (assert (var? rule-var))
  (let [ns (-> rule-var meta :ns)]
    {:var          rule-var
     :ns ns
     :name         (-> rule-var meta :name)
     :input-facts  (->> rule-var deref :lhs lhs->types)
     :output-facts (->> rule-var deref :rhs all-symbols set (keep ctor)
                        (map (partial ns-resolve ns)) doall)
     }))

(defn collect-rules [the-ns]
  (doall (keep analyze-rule (collect-rule-vars the-ns))))

(defn analyze-query [query-var]
  (assert (var? query-var))
  {:var   query-var
   :ns    (-> query-var meta :ns)
   :name  (-> query-var meta :name)
   :facts (->> query-var deref :lhs lhs->types)
   })

(defn collect-queries [the-ns]
  (doall (keep analyze-query (collect-query-vars the-ns))))

(defn collect-facts [the-ns]
  (let [ps (ns-publics the-ns)
        ks (set (keys ps))]
    (doall
     (for [k ks
           :when (.startsWith (name k) "->")
           :when (contains? ks (symbol (str "map" (name k))))
           :let [v      (ns-resolve the-ns k)
                 fields (-> v meta :arglists first)
                 sym    (symbol (.substring (name k) 2))
                 cls    (ns-resolve the-ns sym)]]
       {:type   cls
        :name   sym
        :ns     the-ns
        :fields fields
        }))))

(defn analyze-ns [the-ns]
  {:queries (collect-queries the-ns)
   :rules   (collect-rules the-ns)
   :facts   (collect-facts the-ns)})

(def query-descriptor
  "Default descriptor map for query nodes."
  {:color "#007700", :shape "rect"
   ;; etc other shapes
   })

(defn render-query [query]
  {:id (:name query), :descriptor (assoc query-descriptor
                                         :shape :cds
                                         :margin 0.15
                                         :label (str (:name query)))})

(def fact-descriptor
  "Default descriptor for fact nodes."
  {:color "blue", :shape "rect"})

(defn render-fact [fact]
  {:id (:type fact),
   :descriptor (assoc fact-descriptor
                      :margin 0
                      :shape :plaintext
                      :label  [:TABLE {:BORDER 0
                                       :CELLBORDER 1
                                       :CELLSPACING 0
                                       :CELLPADDING 0
                                       :BGCOLOR "#EEEEEE"}
                               [:TR [:TD (str (:name fact))]]
                               [:TR [:TD
                                     [:TABLE {:BGCOLOR "#FFFFFF"
                                              :BORDER 0
                                              :CELLBORDER 0
                                              :CELLSPACING 0
                                              :CELLPADDING 2}
                                      (for [f (:fields fact)]
                                        [:TR [:TD {:CELLBORDER 0} (str f)]])]]]])})

(def rule-descriptor
  "Default descriptor for rule nodes."
  {:color "brown" :shape "rect"})

(def default-node-descriptor {:shape :box :fontname "Helvetica" :fontsize "11" :margin "0.05" :width 0 :height 0})

(defn render-rule [rule]
  {:id (:name rule),
   :descriptor (assoc rule-descriptor :label (str (:name rule)))})

(defn ns->dot [the-ns]
  (let [{:keys [queries rules facts]} (analyze-ns the-ns)
        id-int (let [a (atom 0)] (memoize (fn [_] (str "n" (swap! a inc)))))
        nodes (concat (keep render-fact facts)
                      (keep render-rule rules)
                      (keep render-query queries))
        edges (let [fact->rule  (for [r rules, f (:input-facts r)] [(id-int f) (id-int (:name r))])
                    rule->fact  (for [r rules, f (:output-facts r)] [(id-int (:name r)) (id-int f)])
                    fact->query (for [q queries, f (:facts q)] [(id-int f) (id-int (:name q))])]
                (doall (concat fact->rule rule->fact fact->query)))
        nodes (distinct (filter (comp (set (concat (map second edges)
                                                   (map first edges)))
                                      id-int :id)
                                nodes))]
    (tangle/graph->dot nodes edges
                       {:node default-node-descriptor
                        :edge {:shape :filled :penwidth 1
                               :arrowhead :normal :arrowsize 0.5}
                        :directed? true
                        :node->id (comp id-int :id)
                        :node->descriptor :descriptor})))

(defn render-to-image-file [the-ns image-file]
  (let [ext "png"]
    (clojure.java.io/copy
     (tangle/dot->image (ns->dot the-ns) ext)
     image-file)))

:ok
