(ns poly.profile.concept
  (:require [poly.profile.utils.gen :refer [generate-object]]))

(defn generate-concepts
  [args]
  (->> (mapcat (fn [[num-kw concept-type]]
                 (if-some [num-concepts (get args num-kw)]
                   (map (fn [idx]
                          (generate-object (assoc args
                                                  :component-number idx
                                                  :component-type concept-type)))
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
