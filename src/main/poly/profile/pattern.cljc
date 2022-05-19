(ns poly.profile.pattern
  (:require [poly.profile.utils.gen :refer [generate-object]]
            [poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))


(defn- generate-pattern-iri-scalar
  [prof-num
   ver-num
   pattern-num
   num-profs
   num-vers
   num-templates
   num-patterns]
  (if (= 0 (rand-int 2))
    (first (iri/create-iri-vec "template"
                               num-profs
                               num-vers
                               num-templates
                               1))
    (first (iri/create-iri-vec prof-num
                               ver-num
                               pattern-num
                               "pattern"
                               num-profs
                               num-vers
                               num-patterns
                               1))))

(defn- generate-pattern-iri-coll
  [prof-num
   ver-num
   pattern-num
   num-profs
   num-vers
   num-templates
   num-patterns
   max-iris]
  (-> (concat
       (iri/create-iri-vec "template"
                           num-profs
                           num-vers
                           num-templates
                           (/ max-iris 2))
       (iri/create-iri-vec prof-num
                           ver-num
                           pattern-num
                           "pattern"
                           num-profs
                           num-vers
                           num-patterns
                           (/ max-iris 2)))
      shuffle))

(defmethod generate-object "Pattern"
  [prof-num
   ver-num
   pattern-num
   pattern-type
   {num-profs :num-profiles
    num-vers  :num-versions
    num-pats  :num-patterns
    num-temps :num-statement-templates
    max-iris  :max-iris}]
  (let [id       (iri/create-iri prof-num ver-num "pattern" pattern-num)
        inscheme (iri/create-iri prof-num ver-num)
        pat-kw   (rand-nth [:sequence :alternates :optional :oneOrMore :zeroOrMore])
        iris     (condp #(contains? %1 %2) pat-kw
                   #{:optional :oneOrMore :zeroOrMore}
                   (loop []
                     (if-some [iri (generate-pattern-iri-scalar prof-num
                                                                ver-num
                                                                pattern-num
                                                                num-profs
                                                                num-vers
                                                                num-temps
                                                                num-pats)]
                       iri
                       (recur)))
                   #{:sequence :alternates}
                   (loop []
                     (let [?iris (generate-pattern-iri-coll prof-num
                                                            ver-num
                                                            pattern-num
                                                            num-profs
                                                            num-vers
                                                            num-temps
                                                            num-pats
                                                            max-iris)]
                       (if (and ?iris (<= 2 (count ?iris)))
                         ?iris
                         (recur)))))]
    {:id         id
     :inScheme   inscheme
     :type       pattern-type
     :prefLabel  {:en-US (format "Pattern %d" pattern-num)}
     :definition {:en-US (format "Pattern Number %d" pattern-num)}
     pat-kw      iris}))

(defn generate-patterns
  [profile-num version-num {:keys [num-patterns] :as args}]
  (not-empty
   (mapv (fn [pattern-num]
           (generate-object profile-num
                            version-num
                            pattern-num
                            "Pattern"
                            args))
         (range num-patterns))))
