(ns poly.profile.concept.resource
  (:require [poly.profile.utils.gen :refer [generate-object]]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(defn- generate-resource-concept
  [{prof-num    :profile-number
    concept-num :component-number
    type-str    :component-type
    type-slug   :component-slug
    type-desc   :component-desc}]
  (let [id             (format "http://example.org/profile-%d/%s-%d" prof-num type-slug concept-num)
        inscheme       (format "http://example.org/profile-%d/v1" prof-num)
        inline-schema? (= 0 (rand-nth [0 1]))]
    (cond-> {:id          id
             :inScheme    inscheme
             :type        type-str
             :prefLabel   {:en-US (format "%s %d" type-desc concept-num)}
             :definition  {:en-US (format "%s Number %d" type-desc concept-num)}
             :contentType "application/json"}
      inline-schema?
      (assoc :inlineSchema "{\"type\": \"number\"}")
      (not inline-schema?)
      (assoc :schema "http://example.org/schema")
      (= 0 (rand-nth [0 1]))
      (assoc :context "http://example.org/context"))))

(defmethod generate-object "StateResource" [args]
  (generate-resource-concept (assoc args
                                    :component-slug "state-resource"
                                    :component-desc "State Resource")))

(defmethod generate-object "AgentProfileResource" [args]
  (generate-resource-concept (assoc args
                                    :component-slug "agent-profile-resource"
                                    :component-desc "Agent Profile Resource")))

(defmethod generate-object "ActivityProfileResource" [args]
  (generate-resource-concept (assoc args
                                    :component-slug "activity-profile-resource"
                                    :component-desc "Activity Profile Resource")))
