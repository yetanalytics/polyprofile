(ns poly.profile.utils.iri
  #?(:cljs (:require [goog.string :refer [format]]
                     [goog.string.format])))

(defn create-iri-vec
  "Create a vector of IRIs (with length limited by `max-iris`) of the form
   ```
   http://poly.profile/profile-[pnum]/[object-slug]-[onum]
   ```
   where `pnum` and `onum` are between 0 (inclusive) and `num-profiles` and
   `num-objects` (exclusive), respectively. Excludes IRIs with the same
   `pnum` and `onum` as `profile-num` and `object-num`, respectively, if they
   are provided, in order to prevent self-loops."
  ([object-slug num-profiles num-objects max-iris]
   (let [num-iris  (rand-int (inc max-iris))
         num-pairs (->> (repeatedly num-iris (fn [] [(rand-int num-profiles)
                                                     (rand-int num-objects)]))
                        distinct)
         iri-coll  (map (fn [[pnum onum]]
                          (format "http://poly.profile/profile-%d/%s-%d"
                                  pnum
                                  object-slug
                                  onum))
                        num-pairs)]
     (->> iri-coll vec not-empty)))
  ([profile-num object-num object-slug num-profiles num-objects max-iris]
   (let [num-iris  (rand-int (inc max-iris))
         num-pairs (->> (repeatedly num-iris (fn [] [(rand-int num-profiles)
                                                     (rand-int num-objects)]))
                        (filter (fn [[pnum onum]] (or (not= profile-num pnum)
                                                      (not= object-num onum))))
                        distinct)
         iri-coll  (map (fn [[pnum onum]]
                          (format "http://poly.profile/profile-%d/%s-%d"
                                  pnum
                                  object-slug
                                  onum))
                        num-pairs)]
     (->> iri-coll vec not-empty))))

(defn create-same-prof-iri-vec
  "Create a vector of IRIs (with length limited by `max-iris`) of the form
   ```
   http://poly.profile/profile-[profile-num]/[object-slug]-[onum]
   ```
   where `onum` is between 0 (inclusive) and `num-objects` (exclusive).
   This ensures that the IRI always points to an item in the same Profile.
   Excludes IRIs with the same `pnum` and `onum` as `profile-num` and
   `object-num`, respectively, to prevent self-loops."
  [profile-num object-num object-slug num-objects max-iris]
  (let [num-iris  (rand-int (inc max-iris))
        comp-nums (->> (repeatedly num-iris (fn [] (rand-int num-objects)))
                       (filter (fn [cn] (not= object-num cn)))
                       distinct)
        iri-coll  (map (fn [cn] (format "http://poly.profile/profile-%d/%s-%d"
                                        profile-num
                                        object-slug
                                        cn))
                       comp-nums)]
    (->> iri-coll vec not-empty)))

(defn create-diff-prof-iri-vec
  "Create a vector of IRIs (with length limited by `max-iris`) of the form
   ```
   http://poly.profile/profile-[pnum]/[object-slug]-[onum]
   ```
   where `pnum` and `onum` are between 0 (inclusive) and `num-profiles` and
   `num-objects` (exclusive), respectively. Excludes IRIs where `pnum` is the
   same as `profile-num`, ensuring that the IRI always points to an item in
   a different Profile."
  [profile-num object-slug num-profiles num-objects max-iris]
  (let [num-iris  (rand-int (inc max-iris))
        num-pairs (->> (repeatedly num-iris (fn [] [(rand-int num-profiles)
                                                    (rand-int num-objects)]))
                       (filter (fn [[pn _]] (not= profile-num pn)))
                       distinct)
        iri-coll  (map (fn [[pn cn]] (format "http://poly.profile/profile-%d/%s-%d"
                                             pn
                                             object-slug
                                             cn))
                       num-pairs)]
    (->> iri-coll vec not-empty)))
