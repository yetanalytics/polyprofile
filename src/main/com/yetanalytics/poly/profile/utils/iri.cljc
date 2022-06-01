(ns com.yetanalytics.poly.profile.utils.iri
  #?(:cljs (:require [goog.string :refer [format]]
                     [goog.string.format])))

(defn create-iri
  "Create a single fixed IRI string. The different arities correspond to
   different use cases: profile ID, version ID/inScheme, and object ID, in
   order of increasing arity. The 4-arity version looks like:
   ```
   http://poly.profile/profile-[profile-num]/v[version-num]/[object-slug]-[object-num]
   ```"
  ([profile-num]
   (format "http://poly.profile/profile-%d"
           profile-num))
  ([profile-num version-num]
   (format "http://poly.profile/profile-%d/v%d"
           profile-num
           version-num))
  ([profile-num version-num object-slug object-num]
   (format "http://poly.profile/profile-%d/v%d/%s-%d"
           profile-num
           version-num
           object-slug
           object-num)))

(defn create-iri-vec
  "Create a vector of IRIs (with length limited by `max-iris`) of the form
   ```
   http://poly.profile/profile-[pnum]/v[vnum]/[object-slug]-[onum]
   ```
   where `pnum`, `vnum`, `onum` have min value 0 (inclusive) and max value
   `num-profiles`, `num-versions`, and `num-objects` (exclusive), respectively.
   If `profile-num`, `version-num,` and `object-num` are provided, then the
   IRI that contains all three of these values is never returned, in order to
   prevent self-loops."
  ([object-slug num-profiles num-versions num-objects max-iris]
   (let [num-iris (rand-int (inc max-iris))
         num-pairs (->> (repeatedly num-iris (fn [] [(rand-int num-profiles)
                                                     (rand-int num-versions)
                                                     (rand-int num-objects)]))
                        distinct)
         iri-coll (map (fn [[pnum vnum onum]]
                         (format "http://poly.profile/profile-%d/v%d/%s-%d"
                                 pnum
                                 vnum
                                 object-slug
                                 onum))
                       num-pairs)]
     (->> iri-coll vec not-empty)))
  ([profile-num version-num object-num object-slug num-profiles num-versions num-objects max-iris]
   (let [num-iris (rand-int (inc max-iris))
         num-pairs (->> (repeatedly num-iris (fn [] [(rand-int num-profiles)
                                                     (rand-int num-versions)
                                                     (rand-int num-objects)]))
                        (filter (fn [[pnum vnum onum]]
                                  (or (not= profile-num pnum)
                                      (not= version-num vnum)
                                      (not= object-num onum))))
                        distinct)
         iri-coll (map (fn [[pnum vnum onum]]
                         (format "http://poly.profile/profile-%d/v%d/%s-%d"
                                 pnum
                                 vnum
                                 object-slug
                                 onum))
                       num-pairs)]
     (->> iri-coll vec not-empty))))

;; Used for Patterns, which require `min-iris` and in general do not care about
;; IRI distinctiveness.
(defn create-nondistinct-iri-vec
  "Similar to `create-iri-vec`, but with two differences:
   - A `min-iris` arg is required to set the minimum number of IRIs in the coll.
   - Repeated IRIs are not removed."
  [object-slug num-profiles num-versions num-objects min-iris max-iris]
  (let [num-iris  (+ min-iris (rand-int (- (inc max-iris) min-iris)))
        num-pairs (repeatedly num-iris (fn [] [(rand-int num-profiles)
                                               (rand-int num-versions)
                                               (rand-int num-objects)]))

        iri-coll  (map (fn [[pnum vnum onum]]
                         (format "http://poly.profile/profile-%d/v%d/%s-%d"
                                 pnum
                                 vnum
                                 object-slug
                                 onum))
                       num-pairs)]
    (->> iri-coll vec not-empty)))

(defn create-same-version-iri-vec
  "Create a vector of IRIs (with length limited by `max-iris`) of the form
   ```
   http://poly.profile/profile-[profile-num]/v[version-num]/[object-slug]-[onum]
   ```
   where `onum` is between 0 (inclusive) and `num-objects` (exclusive).
   This ensures that the IRI always points to an item in the same Profile
   version. Excludes IRIs that have the same `onum` value as `object-num`,
   in order to prevent self-loops."
  [profile-num version-num object-num object-slug num-objects max-iris]
  (let [num-iris  (rand-int (inc max-iris))
        comp-nums (->> (repeatedly num-iris (fn [] (rand-int num-objects)))
                       (filter (fn [onum] (not= object-num onum)))
                       distinct)
        iri-coll  (map (fn [onum]
                         (format "http://poly.profile/profile-%d/v%d/%s-%d"
                                 profile-num
                                 version-num
                                 object-slug
                                 onum))
                       comp-nums)]
    (->> iri-coll vec not-empty)))

(defn create-diff-version-iri-vec
  "Create a vector of IRIs (with length limited by `max-iris`) of the form
   ```
   http://poly.profile/profile-[pnum]/v[vnum]/[object-slug]-[onum]
   ```
   where `pnum`, `vnum`, and `onum` start at 0 (inclusive) and have max values
   `num-profiles`, `num-versions`, and `num-objects` (exclusive), respectively.
   Excludes IRIs where `pnum` is the same as `profile-num` and where `vnum` is
   the same as `version-num`, ensuring that the IRI always points to an item in
   a different Profile version."
  [profile-num version-num object-slug num-profiles num-versions num-objects max-iris]
  (let [num-iris  (rand-int (inc max-iris))
        num-pairs (->> (repeatedly num-iris (fn [] [(rand-int num-profiles)
                                                    (rand-int num-versions)
                                                    (rand-int num-objects)]))
                       (filter (fn [[pnum vnum _]]
                                 (or (not= profile-num pnum)
                                     (not= version-num vnum))))
                       distinct)
        iri-coll  (map (fn [[pnum vnum onum]]
                         (format "http://poly.profile/profile-%d/v%d/%s-%d"
                                 pnum
                                 vnum
                                 object-slug
                                 onum))
                       num-pairs)]
    (->> iri-coll vec not-empty)))

(defn create-diff-profile-iri-vec
  "Create a vector of IRIs (with length limited by `max-iris`) of the form
   ```
   http://poly.profile/profile-[pnum]/v[vnum]/[object-slug]-[onum]
   ```
   where `pnum`, `vnum`, and `onum` start at 0 (inclusive) and have max values
   `num-profiles`, `num-versions`, and `num-objects` (exclusive), respectively.
   Excludes IRIs where `pnum` is the same as `profile-num`, ensuring that the
   IRI always points to an item in a different Profile."
  [profile-num object-slug num-profiles num-versions num-objects max-iris]
  (let [num-iris  (rand-int (inc max-iris))
        num-pairs (->> (repeatedly num-iris (fn [] [(rand-int num-profiles)
                                                    (rand-int num-versions)
                                                    (rand-int num-objects)]))
                       (filter (fn [[pn _]] (not= profile-num pn)))
                       distinct)
        iri-coll  (map (fn [[pnum vnum onum]]
                         (format "http://poly.profile/profile-%d/v%d/%s-%d"
                                 pnum
                                 vnum
                                 object-slug
                                 onum))
                       num-pairs)]
    (->> iri-coll vec not-empty)))
