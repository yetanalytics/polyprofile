(ns poly.profile.concept.extension
  (:require [poly.profile.utils.gen :refer [generate-object]]
            [poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(defn- generate-extension-concept
  [prof-num
   concept-num
   concept-type
   concept-slug
   concept-desc
   num-profiles
   num-targets
   target-slug
   max-iris]
  (let [id             (format "http://example.org/profile-%d/%s-%d" prof-num concept-slug concept-num)
        inscheme       (format "http://example.org/profile-%d/v1" prof-num)
        inline-schema? (= 0 (rand-nth [0 1]))
        ?rec-iris      (iri/create-iri-vec target-slug num-profiles num-targets max-iris)]
    (cond-> {:id         id
             :inScheme   inscheme
             :type       concept-type
             :prefLabel  {:en-US (format "%s %d" concept-desc concept-num)}
             :definition {:en-US (format "%s Number %d" concept-desc concept-num)}}
      inline-schema?
      (assoc :inlineSchema "{\"type\": \"number\"}")
      (not inline-schema?)
      (assoc :schema "http://example.org/schema")
      (= 0 (rand-nth [0 1]))
      (assoc :context "http://example.org/context")
      (and ?rec-iris
           (= "activity-type" target-slug))
      (assoc :recommendedActivityTypes ?rec-iris)
      (and ?rec-iris
           (= "verb" target-slug))
      (assoc :recommendedVerbs ?rec-iris))))

(defmethod generate-object "ActivityExtension" [profile-num
                                                activity-ext-num
                                                activity-ext-type
                                                {:keys [num-profiles
                                                        num-activity-types
                                                        max-iris]}]
  (generate-extension-concept profile-num
                              activity-ext-num
                              activity-ext-type
                              "activity-extension"
                              "Activity Extension"
                              num-profiles
                              num-activity-types
                              "activity-type"
                              max-iris))

(defmethod generate-object "ContextExtension" [profile-num
                                               context-ext-num
                                               context-ext-type
                                               {:keys [num-profiles
                                                       num-verbs
                                                       max-iris]}]
  (generate-extension-concept profile-num
                              context-ext-num
                              context-ext-type
                              "context-extension"
                              "Context Extension"
                              num-profiles
                              num-verbs
                              "verb"
                              max-iris))

(defmethod generate-object "ResultExtension" [profile-num
                                              result-ext-num
                                              result-ext-type
                                              {:keys [num-profiles
                                                      num-verbs
                                                      max-iris]}]
  (generate-extension-concept profile-num
                              result-ext-num
                              result-ext-type
                              "result-extension"
                              "Result Extension"
                              num-profiles
                              num-verbs
                              "verb"
                              max-iris))