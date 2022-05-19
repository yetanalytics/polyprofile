(ns poly.profile.concept.basic
  (:require [poly.profile.utils.gen :refer [generate-object]]
            [poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(defn- generate-basic-concept
  [prof-num
   concept-num
   concept-type
   concept-slug
   concept-desc
   num-profs
   num-concepts
   max-iris]
  (let [id            (format "http://example.org/profile-%d/%s-%d" prof-num concept-slug concept-num)
        inscheme      (format "http://example.org/profile-%d/v1" prof-num)
        ?broader      (iri/create-same-prof-iri-vec prof-num concept-num concept-slug num-concepts max-iris)
        ?narrower     (iri/create-same-prof-iri-vec prof-num concept-num concept-slug num-concepts max-iris)
        ?related      (iri/create-same-prof-iri-vec prof-num concept-num concept-slug num-concepts max-iris)
        ?broadMatch   (iri/create-diff-prof-iri-vec prof-num concept-slug num-profs num-concepts max-iris)
        ?narrowMatch  (iri/create-diff-prof-iri-vec prof-num concept-slug num-profs num-concepts max-iris)
        ?relatedMatch (iri/create-diff-prof-iri-vec prof-num concept-slug num-profs num-concepts max-iris)
        ?exactMatch   (iri/create-diff-prof-iri-vec prof-num concept-slug num-profs num-concepts max-iris)]
    (cond-> {:id         id
             :inScheme   inscheme
             :type       concept-type
             :prefLabel  {:en-US (format "%s %d" concept-desc concept-num)}
             :definition {:en-US (format "%s Number %d" concept-desc concept-num)}}
      (not-empty ?broader)
      (assoc :broader ?broader)
      (not-empty ?narrower)
      (assoc :narrower ?narrower)
      (not-empty ?related)
      (assoc :related ?related
             :deprecated true)
      (not-empty ?broadMatch)
      (assoc :broadMatch ?broadMatch)
      (not-empty ?narrowMatch)
      (assoc :narrowMatch ?narrowMatch)
      (not-empty ?relatedMatch)
      (assoc :relatedMatch ?relatedMatch)
      (not-empty ?exactMatch)
      (assoc :exactMatch ?exactMatch))))

(defmethod generate-object "Verb" [prof-num
                                   verb-num
                                   verb-type
                                   {:keys [num-profiles
                                           num-verbs
                                           max-iris]}]
  (generate-basic-concept prof-num
                          verb-num
                          verb-type
                          "verb"
                          "Verb"
                          num-profiles
                          num-verbs
                          max-iris))

(defmethod generate-object "ActivityType" [prof-num
                                           activity-type-num
                                           activity-type-type
                                           {:keys [num-profiles
                                                   num-activity-types
                                                   max-iris]}]
  (generate-basic-concept prof-num
                          activity-type-num
                          activity-type-type
                          "activity-type"
                          "Activity Type"
                          num-profiles
                          num-activity-types
                          max-iris))

(defmethod generate-object "AttachmentUsageType"
  [prof-num
   attachment-usage-type-num
   attachment-usage-type-type
   {:keys [num-profiles
           num-attachment-usage-types
           max-iris]}]
  (generate-basic-concept prof-num
                          attachment-usage-type-num
                          attachment-usage-type-type
                          "attachment-usage-type"
                          "Attachment Usage Type"
                          num-profiles
                          num-attachment-usage-types
                          max-iris))
