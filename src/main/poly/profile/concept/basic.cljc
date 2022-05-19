(ns poly.profile.concept.basic
  (:require [poly.profile.utils.gen :refer [generate-object]]
            [poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(defn- generate-basic-concept
  [prof-num
   ver-num
   concept-num
   concept-type
   concept-slug
   concept-desc
   num-profs
   num-vers
   num-concepts
   max-iris]
  (let [;; IRI functions (wrap in thunks for formatting)
        create-same-ver-iris
        #(iri/create-same-version-iri-vec prof-num
                                          concept-num
                                          concept-slug
                                          num-concepts
                                          num-vers
                                          max-iris)
        create-diff-prof-iris
        #(iri/create-diff-profile-iri-vec prof-num
                                          concept-slug
                                          num-profs
                                          num-concepts
                                          num-vers
                                          max-iris)
        create-diff-ver-iris
        #(iri/create-diff-version-iri-vec prof-num
                                          ver-num
                                          concept-slug
                                          num-profs
                                          num-concepts
                                          num-vers
                                          max-iris)
        ;; Values
        id             (iri/create-iri prof-num ver-num concept-slug concept-num)
        inscheme       (iri/create-iri prof-num ver-num)
        ?broader       (create-same-ver-iris)
        ?narrower      (create-same-ver-iris)
        ?related       (create-same-ver-iris)
        ?broadMatch    (create-diff-prof-iris)
        ?narrowMatch   (create-diff-prof-iris)
        ?relatedMatch  (create-diff-ver-iris)
        ?exactMatch    (create-diff-ver-iris)]
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
                                   ver-num
                                   verb-num
                                   verb-type
                                   {:keys [num-profiles
                                           num-versions
                                           num-verbs
                                           max-iris]}]
  (generate-basic-concept prof-num
                          ver-num
                          verb-num
                          verb-type
                          "verb"
                          "Verb"
                          num-profiles
                          num-versions
                          num-verbs
                          max-iris))

(defmethod generate-object "ActivityType" [prof-num
                                           ver-num
                                           activity-type-num
                                           activity-type-type
                                           {:keys [num-profiles
                                                   num-versions
                                                   num-activity-types
                                                   max-iris]}]
  (generate-basic-concept prof-num
                          ver-num
                          activity-type-num
                          activity-type-type
                          "activity-type"
                          "Activity Type"
                          num-profiles
                          num-versions
                          num-activity-types
                          max-iris))

(defmethod generate-object "AttachmentUsageType" [prof-num
                                                  ver-num
                                                  att-use-type-num
                                                  att-use-type-type
                                                  {:keys [num-profiles
                                                          num-versions
                                                          num-attachment-usage-types
                                                          max-iris]}]
  (generate-basic-concept prof-num
                          ver-num
                          att-use-type-num
                          att-use-type-type
                          "attachment-usage-type"
                          "Attachment Usage Type"
                          num-profiles
                          num-versions
                          num-attachment-usage-types
                          max-iris))
