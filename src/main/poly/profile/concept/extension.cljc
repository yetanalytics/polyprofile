(ns poly.profile.concept.extension
  (:require [poly.profile.utils.gen :refer [generate-object]]
            [poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(defn- generate-extension-concept
  [prof-num
   ver-num
   concept-num
   concept-type
   concept-slug
   concept-desc
   num-profiles
   num-versions
   num-targets
   target-slug
   max-iris]
  (let [id        (iri/create-iri prof-num ver-num concept-slug concept-num)
        inscheme  (iri/create-iri prof-num ver-num)
        ischema?  (= 0 (rand-nth [0 1]))
        ?rec-iris (iri/create-iri-vec target-slug
                                      num-profiles
                                      num-versions
                                      num-targets
                                      max-iris)]
    (cond-> {:id         id
             :inScheme   inscheme
             :type       concept-type
             :prefLabel  {:en-US (format "%s %d" concept-desc concept-num)}
             :definition {:en-US (format "%s Number %d" concept-desc concept-num)}}
      ischema?
      (assoc :inlineSchema "{\"type\": \"number\"}")
      (not ischema?)
      (assoc :schema "http://poly.profile/schema")
      (= 0 (rand-nth [0 1]))
      (assoc :context "http://poly.profile/context")
      (and ?rec-iris
           (= "activity-type" target-slug))
      (assoc :recommendedActivityTypes ?rec-iris)
      (and ?rec-iris
           (= "verb" target-slug))
      (assoc :recommendedVerbs ?rec-iris))))

(defmethod generate-object "ActivityExtension" [profile-num
                                                ver-num
                                                activity-ext-num
                                                activity-ext-type
                                                {:keys [num-profiles
                                                        num-versions
                                                        num-activity-types
                                                        max-iris]}]
  (generate-extension-concept profile-num
                              ver-num
                              activity-ext-num
                              activity-ext-type
                              "activity-extension"
                              "Activity Extension"
                              num-profiles
                              num-versions
                              num-activity-types
                              "activity-type"
                              max-iris))

(defmethod generate-object "ContextExtension" [profile-num
                                               ver-num
                                               context-ext-num
                                               context-ext-type
                                               {:keys [num-profiles
                                                       num-versions
                                                       num-verbs
                                                       max-iris]}]
  (generate-extension-concept profile-num
                              ver-num
                              context-ext-num
                              context-ext-type
                              "context-extension"
                              "Context Extension"
                              num-profiles
                              num-versions
                              num-verbs
                              "verb"
                              max-iris))

(defmethod generate-object "ResultExtension" [profile-num
                                              ver-num
                                              result-ext-num
                                              result-ext-type
                                              {:keys [num-profiles
                                                      num-versions
                                                      num-verbs
                                                      max-iris]}]
  (generate-extension-concept profile-num
                              ver-num
                              result-ext-num
                              result-ext-type
                              "result-extension"
                              "Result Extension"
                              num-profiles
                              num-versions
                              num-verbs
                              "verb"
                              max-iris))
