(ns poly.profile.concept
  (:require
   ;; Need to ensure that the concept namespaces are loaded for multi-method
   ;; to dispatch properly
   [poly.profile.concept.activity]
   [poly.profile.concept.basic]
   [poly.profile.concept.extension]
   [poly.profile.concept.resource]
   [poly.profile.utils.gen :refer [generate-object]]))

(defn generate-concepts
  "Generate a vector of concepts, or `nil` if empty."
  [profile-num version-num args]
  (->> (mapcat (fn [[num-kw concept-type]]
                 (if-some [num-concepts (get args num-kw)]
                   (map (fn [concept-num]
                          (generate-object profile-num
                                           version-num
                                           concept-num
                                           concept-type
                                           args))
                        (range num-concepts))
                   '()))
               [[:num-verbs "Verb"]
                [:num-activity-types "ActivityType"]
                [:num-attachment-usage-types "AttachmentUsageType"]
                [:num-activity-extensions "ActivityExtension"]
                [:num-context-extensions "ContextExtension"]
                [:num-result-extensions "ResultExtension"]
                [:num-state-resources "StateResource"]
                [:num-agent-profile-resources "AgentProfileResource"]
                [:num-activity-profile-resources "ActivityProfileResource"]
                [:num-activities "Activity"]])
       vec
       not-empty))
