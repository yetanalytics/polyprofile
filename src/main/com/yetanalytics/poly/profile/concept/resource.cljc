(ns com.yetanalytics.poly.profile.concept.resource
  (:require [com.yetanalytics.poly.profile.utils.gen :refer [generate-object]]
            [com.yetanalytics.poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(defn- generate-resource-concept
  [prof-num
   ver-num
   concept-num
   type-str
   type-slug
   type-desc]
  (let [id       (iri/create-iri prof-num ver-num type-slug concept-num)
        inscheme (iri/create-iri prof-num ver-num)
        ischema? (= 0 (rand-nth [0 1]))]
    (cond-> {:id          id
             :inScheme    inscheme
             :type        type-str
             :prefLabel   {:en-US (format "%s %d" type-desc concept-num)}
             :definition  {:en-US (format "%s Number %d" type-desc concept-num)}
             :contentType "application/json"}
      ischema?
      (assoc :inlineSchema "{\"type\": \"number\"}")
      (not ischema?)
      (assoc :schema "http://poly.profile/schema")
      (= 0 (rand-nth [0 1]))
      (assoc :context "http://poly.profile/context"))))

(defmethod generate-object "StateResource" [profile-num
                                            ver-num
                                            state-res-num
                                            state-res-type
                                            _args]
  (generate-resource-concept profile-num
                             ver-num
                             state-res-num
                             state-res-type
                             "state-resource"
                             "State Resource"))

(defmethod generate-object "AgentProfileResource" [profile-num
                                                   ver-num
                                                   agent-prof-res-num
                                                   agent-prof-res-type
                                                   _args]
  (generate-resource-concept profile-num
                             ver-num
                             agent-prof-res-num
                             agent-prof-res-type
                             "agent-profile-resource"
                             "Agent Profile Resource"))

(defmethod generate-object "ActivityProfileResource" [profile-num
                                                      ver-num
                                                      activity-prof-res-num
                                                      activity-prof-res-type
                                                      _args]
  (generate-resource-concept profile-num
                             ver-num
                             activity-prof-res-num
                             activity-prof-res-type
                             "activity-profile-resource"
                             "Activity Profile Resource"))
