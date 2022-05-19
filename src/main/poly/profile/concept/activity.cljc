(ns poly.profile.concept.activity
  (:require [poly.profile.utils.gen :refer [generate-object]]
            [poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
              [goog.string.format]])))

(defmethod generate-object "Activity"
  [prof-num
   ver-num
   concept-num
   type-str
   {:keys [num-profiles
           num-versions
           num-activity-types]}]
  (let [id         (iri/create-iri prof-num ver-num "activity" concept-num)
        inscheme   (iri/create-iri prof-num ver-num)
        ?atype-iri (first (iri/create-iri-vec "activity"
                                              num-profiles
                                              num-versions
                                              num-activity-types
                                              1))]
    (cond-> {:id                 id
             :inScheme           inscheme
             :type               type-str
             :activityDefinition {:name        {:en-US (format "%s %d" type-str concept-num)}
                                  :description {:en-US (format "%s Number %d" type-str concept-num)}
                                  :_context    "https://w3id.org/xapi/profiles/activity-context"}}
      ?atype-iri
      (assoc-in [:activityDefinition :type] ?atype-iri))))
