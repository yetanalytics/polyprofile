(ns poly.profile.concept.extension
  (:require [poly.profile.utils.gen :refer [generate-object]]
            [poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(defn- generate-extension-concept
  [{prof-num     :profile-number
    concept-num  :component-number
    num-targets  :num-targets
    type-str     :component-type
    type-slug    :component-slug
    type-desc    :component-desc
    tgt-slug     :target-slug
    :as args}]
  (let [args*          (assoc args
                              :num-components num-targets
                              :component-slug tgt-slug)
        id             (format "http://example.org/profile-%d/%s-%d" prof-num type-slug concept-num)
        inscheme       (format "http://example.org/profile-%d/v1" prof-num)
        inline-schema? (= 0 (rand-nth [0 1]))
        ?rec-iris      (iri/create-iri-vec args*)]
    (cond-> {:id         id
             :inScheme   inscheme
             :type       type-str
             :prefLabel  {:en-US (format "%s %d" type-desc concept-num)}
             :definition {:en-US (format "%s Number %d" type-desc concept-num)}}
      inline-schema?
      (assoc :inlineSchema "{\"type\": \"number\"}")
      (not inline-schema?)
      (assoc :schema "http://example.org/schema")
      (= 0 (rand-nth [0 1]))
      (assoc :context "http://example.org/context")
      (and ?rec-iris
           (= "activity-type" tgt-slug))
      (assoc :recommendedActivityTypes ?rec-iris)
      (and ?rec-iris
           (= "verb" tgt-slug))
      (assoc :recommendedVerbs ?rec-iris))))

(defmethod generate-object "ActivityExtension" [args]
  (generate-extension-concept (assoc args
                                     :num-targets (:num-activity-types args)
                                     :component-slug "activity-extension"
                                     :component-desc "Activity Extension"
                                     :target-slug "activity-type")))

(defmethod generate-object "ContextExtension" [args]
  (generate-extension-concept (assoc args
                                     :num-targets (:num-verbs args)
                                     :component-slug "context-extension"
                                     :component-desc "Context Extension"
                                     :target-slug "verb")))

(defmethod generate-object "ResultExtension" [args]
  (generate-extension-concept (assoc args
                                     :num-targets (:num-verbs args)
                                     :component-slug "result-extension"
                                     :component-desc "Result Extension"
                                     :target-slug "verb")))
