(ns com.yetanalytics.poly.profile.pattern
  (:require [clojure.set :as cset]
            [com.yetanalytics.poly.profile.utils.gen :refer [generate-object]]
            [com.yetanalytics.poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

;; Templates can be any Profile from the cosmos, but sub-Patterns can only
;; be from the same Profile version, in order to allow for easy lazy Profile
;; generation.

(defn update-ancestors-map
  "Given a `ancestors-map` between Pattern IDs and their ancestors (including
   themselves), compute the updated map given `pattern-id` and its
   `sub-pattern-ids`."
  [ancestors-map pattern-id sub-pattern-ids]
  (let [ancestor-ids (-> ancestors-map
                         (get pattern-id)
                         (conj pattern-id)
                         set)]
    (reduce (fn [m sub-pat-id]
              (if (contains? m sub-pat-id)
                (update m sub-pat-id cset/union ancestor-ids)
                (assoc m sub-pat-id (conj ancestor-ids sub-pat-id))))
            ancestors-map
            (conj sub-pattern-ids pattern-id))))

(comment
  ;; Examples of tracking ancestors/transitive closures

  ;; Add 0 --> 1 and 0 --> 2
  (= {0 #{0}
      1 #{0 1}
      2 #{0 2}}
     (-> {}
         (update-ancestors-map 0 #{1 2})))
  
  ;; Add 1 --> 3; 3 gets 1's ancestors
  (= {0 #{0}
      1 #{0 1}
      2 #{0 2}
      3 #{0 1 3}}
     (-> {}
         (update-ancestors-map 0 #{1 2})
         (update-ancestors-map 1 #{3})))

  ;; Add 2 --> 3; 3 gets 2's (as well as 1's) ancestors
  (= {0 #{0}
      1 #{0 1}
      2 #{0 2}
      3 #{0 1 2 3}}
     (-> {}
         (update-ancestors-map 0 #{1 2})
         (update-ancestors-map 1 #{3})
         (update-ancestors-map 2 #{3}))))

(defn- init-scalar-pattern
  [pattern-base
   pattern-kw
   num-profs
   num-vers
   num-templates]
  (assoc pattern-base
         pattern-kw (first (iri/create-nondistinct-iri-vec "template"
                                                           num-profs
                                                           num-vers
                                                           num-templates
                                                           1
                                                           1))))

(defn- init-coll-pattern
  [pattern-base
   pattern-kw
   num-profs
   num-vers
   num-templates
   max-iris]
  (assoc pattern-base
         pattern-kw (iri/create-nondistinct-iri-vec "template"
                                                    num-profs
                                                    num-vers
                                                    num-templates
                                                    max-iris
                                                    2)))

(defn- add-patterns
  "Add a single Pattern DAG to `pattern-coll` by designating a random Pattern
   to be the primary pattern. Sub-patterns are then added in a DFS fashion,
   avoiding self-loops by tracking the ancestors of the visited Pattern.
   
   NOTE: Will throw an exception if `pattern-coll` is empty."
  [pattern-coll
   prof-num
   ver-num
   num-patterns
   max-iris]
  (let [{fst-pat-id :id :as first-pattern} (rand-nth pattern-coll)]
    (loop [patterns     (list (assoc first-pattern :primary true))
           updated-pats (-> (reduce (fn [m pat] (assoc m (:id pat) pat))
                                    {}
                                    pattern-coll)
                            (update fst-pat-id assoc :primary true))
           ancestors-m  {fst-pat-id #{fst-pat-id}}]
      (if-some [{pat-id :id :as pattern} (peek patterns)]
        (let [tclose (get ancestors-m pat-id)]
          (cond
            (contains? pattern :sequence)
            (let [;; We can pass -1 as the pattern number since self-looping
                  ;; IRIs will be excluded later on
                  seq-pats
                  (->> (iri/create-same-version-iri-vec prof-num
                                                        ver-num
                                                        -1
                                                        "pattern"
                                                        num-patterns
                                                        max-iris)
                       (filter #(not (contains? tclose %))))
                  seq-all
                  (->> seq-pats
                       (concat (:sequence pattern))
                       shuffle
                       (take max-iris)
                       vec)
                  new-pat
                  (assoc pattern :sequence seq-all)]
              (recur (apply conj
                            (pop patterns)
                            (map (partial get updated-pats) seq-pats))
                     (assoc updated-pats pat-id new-pat)
                     (update-ancestors-map ancestors-m pat-id seq-pats)))
            (contains? pattern :alternates)
            (let [alt-pats
                  (->> (iri/create-same-version-iri-vec prof-num
                                                        ver-num
                                                        -1
                                                        "pattern"
                                                        num-patterns
                                                        max-iris)
                       (filter (fn [sub-pat-id]
                                 (not (contains? tclose sub-pat-id))))
                       (filter (fn [sub-pat-id]
                                 (let [sub (get updated-pats sub-pat-id)]
                                   (not (or (contains? sub :optional)
                                            (contains? sub :zeroOrMore)))))))
                  alt-all
                  (->> alt-pats
                       (concat (:alternates pattern))
                       shuffle
                       (take max-iris)
                       vec)
                  new-pat
                  (assoc pattern :alternates alt-all)]
              (recur (apply conj
                            (pop patterns)
                            (map (partial get updated-pats) alt-pats))
                     (assoc updated-pats pat-id new-pat)
                     (update-ancestors-map ancestors-m pat-id alt-pats)))
            :else ; optional, oneOrMore, zeroOrMore
            (if-some [sub-pat-id
                      (->> (iri/create-same-version-iri-vec prof-num
                                                            ver-num
                                                            -1
                                                            "pattern"
                                                            num-patterns
                                                            1)
                           (filter (fn [sub-pat-id]
                                     (not (contains? tclose sub-pat-id))))
                           first)]
              (let [pat-kw  (-> pattern
                                (select-keys [:optional :oneOrMore :zeroOrMore])
                                keys
                                first)
                    new-pat (assoc pattern pat-kw sub-pat-id)]
                (recur (conj (pop patterns)
                             (get updated-pats sub-pat-id))
                       (assoc updated-pats pat-id new-pat)
                       (update-ancestors-map ancestors-m pat-id #{sub-pat-id})))
              (recur (rest patterns)
                     updated-pats
                     ancestors-m))))
        (vec (vals updated-pats))))))

;; Initial Patterns only contain Templates.
(defmethod generate-object "Pattern"
  [prof-num
   ver-num
   pattern-num
   pattern-type
   {num-profs :num-profiles
    num-vers  :num-versions
    num-temps :num-statement-templates
    max-iris  :max-iris}]
  (let [id       (iri/create-iri prof-num ver-num "pattern" pattern-num)
        inscheme (iri/create-iri prof-num ver-num)
        pat-kw   (rand-nth [:sequence :alternates :optional :oneOrMore :zeroOrMore])
        pat-base {:id         id
                  :inScheme   inscheme
                  :type       pattern-type
                  :prefLabel  {:en-US (format "Pattern %d" pattern-num)}
                  :definition {:en-US (format "Pattern Number %d" pattern-num)}}]
    (condp #(contains? %1 %2) pat-kw
      #{:optional :oneOrMore :zeroOrMore}
      (init-scalar-pattern pat-base pat-kw num-profs num-vers num-temps)
      #{:sequence :alternates}
      (init-coll-pattern pat-base pat-kw num-profs num-vers num-temps max-iris))))

(defn generate-patterns
  "Generate a vector of templates, or `nil` if empty."
  [profile-num version-num {:keys [num-patterns max-iris] :as args}]
  (let [pattern-bases (map (fn [pattern-num]
                             (generate-object profile-num
                                              version-num
                                              pattern-num
                                              "Pattern"
                                              args))
                           (range num-patterns))]
    (when (not-empty pattern-bases)
      (add-patterns pattern-bases
                    profile-num
                    version-num
                    num-patterns
                    max-iris))))
