(ns poly.profile.concept.resource
  (:require [poly.profile.utils.gen :refer [generate-object]]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(defn- generate-resource-concept
  [prof-num
   concept-num
   type-str
   type-slug
   type-desc]
  (let [id             (format "http://poly.profile/profile-%d/%s-%d" prof-num type-slug concept-num)
        inscheme       (format "http://poly.profile/profile-%d/v1" prof-num)
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
      (assoc :schema "http://poly.profile/schema")
      (= 0 (rand-nth [0 1]))
      (assoc :context "http://poly.profile/context"))))

(defmethod generate-object "StateResource" [profile-num
                                            state-res-num
                                            state-res-type
                                            _args]
  (generate-resource-concept profile-num
                             state-res-num
                             state-res-type
                             "state-resource"
                             "State Resource"))

(defmethod generate-object "AgentProfileResource" [profile-num
                                                   agent-prof-res-num
                                                   agent-prof-res-type
                                                   _args]
  (generate-resource-concept profile-num
                             agent-prof-res-num
                             agent-prof-res-type
                             "agent-profile-resource"
                             "Agent Profile Resource"))

(defmethod generate-object "ActivityProfileResource" [profile-num
                                                      activity-prof-res-num
                                                      activity-prof-res-type
                                                      _args]
  (generate-resource-concept profile-num
                             activity-prof-res-num
                             activity-prof-res-type
                             "activity-profile-resource"
                             "Activity Profile Resource"))
