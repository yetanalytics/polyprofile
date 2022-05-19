(ns poly.profile.concept.basic
  (:require [poly.profile.utils.gen :refer [generate-object]]
            [poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(defn- generate-basic-concept
  [{prof-num     :profile-number
    concept-num  :component-number
    type-str     :component-type
    type-slug    :component-slug
    type-desc    :component-desc
    :as args}]
  (let [id            (format "http://example.org/profile-%d/%s-%d" prof-num type-slug concept-num)
        inscheme      (format "http://example.org/profile-%d/v1" prof-num)
        ?broader      (iri/create-same-prof-iri-vec args)
        ?narrower     (iri/create-same-prof-iri-vec args)
        ?related      (iri/create-same-prof-iri-vec args)
        ?broadMatch   (iri/create-diff-prof-iri-vec args)
        ?narrowMatch  (iri/create-diff-prof-iri-vec args)
        ?relatedMatch (iri/create-diff-prof-iri-vec args)
        ?exactMatch   (iri/create-diff-prof-iri-vec args)]
    (cond-> {:id         id
             :inScheme   inscheme
             :type       type-str
             :prefLabel  {:en-US (format "%s %d" type-desc concept-num)}
             :definition {:en-US (format "%s Number %d" type-desc concept-num)}}
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

(defmethod generate-object "Verb" [args]
  (generate-basic-concept (assoc args
                                 :num-components (:num-verbs args)
                                 :component-slug "verb"
                                 :component-desc "Verb")))

(defmethod generate-object "ActivityType" [args]
  (generate-basic-concept (assoc args
                                 :num-components (:num-activity-types args)
                                 :component-slug "activity-type"
                                 :component-desc "Activity Type")))

(defmethod generate-object "AttachmentUsageType" [args]
  (generate-basic-concept (assoc args
                                 :num-components (:num-attachment-usage-types args)
                                 :component-slug "attachment-usage-type"
                                 :component-desc "Attachment Usage Type")))
