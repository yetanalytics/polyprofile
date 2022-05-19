(ns poly.profile.utils.iri
  (:require #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(defn create-iri-vec
  [{prof-num :profile-number
    comp-num :component-number
    comp-str :component-slug
    num-prof :num-profiles
    num-comp :num-components
    max-iris :max-iris
    :or {num-prof 1
         num-comp 0
         max-iris 5}}]
  (let [num-iris  (rand-int (inc max-iris))
        num-pairs (->> (repeatedly num-iris (fn [] [(rand-int num-prof)
                                                    (rand-int num-comp)]))
                       (filter (fn [[pn cn]] (or (not= prof-num pn)
                                                 (not= comp-num cn))))
                       distinct)
        iri-coll  (map (fn [[pn cn]] (format "http://poly.profile/profile-%d/%s-%d"
                                             pn
                                             comp-str
                                             cn))
                       num-pairs)]
    (->> iri-coll vec not-empty)))

(defn create-same-prof-iri-vec
  [{prof-num :profile-number
    comp-num :component-number
    comp-str :component-slug
    num-comp :num-components
    max-iris :max-iris
    :or {num-comp 0
         max-iris 5}}]
  (let [num-iris  (rand-int (inc max-iris))
        comp-nums (->> (repeatedly num-iris (fn [] (rand-int num-comp)))
                       (filter (fn [cn] (not= comp-num cn)))
                       distinct)
        iri-coll  (map (fn [cn] (format "http://poly.profile/profile-%d/%s-%d"
                                        prof-num
                                        comp-str
                                        cn))
                       comp-nums)]
    (->> iri-coll vec not-empty)))

(defn create-diff-prof-iri-vec
  [{prof-num :profile-number
    comp-str :component-slug
    num-prof :num-profiles
    num-comp :num-components
    max-iris :max-iris
    :or {num-prof 1
         num-comp 0
         max-iris 5}}]
  (let [num-iris  (rand-int (inc max-iris))
        num-pairs (->> (repeatedly num-iris (fn [] [(rand-int num-prof)
                                                    (rand-int num-comp)]))
                       (filter (fn [[pn _]] (not= prof-num pn)))
                       distinct)
        iri-coll  (map (fn [[pn cn]] (format "http://poly.profile/profile-%d/%s-%d"
                                             pn
                                             comp-str
                                             cn))
                       num-pairs)]
    (->> iri-coll vec not-empty)))
