(ns poly.profile.concept.activity
  (:require [poly.profile.utils.gen :refer [generate-object]]
            [poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
              [goog.string.format]])))

(defmethod generate-object "Activity"
  [prof-num
   concept-num
   type-str
   args]
  (let [id         (format "http://example.org/profile-%d/activity-%d" prof-num concept-num)
        inscheme   (format "http://example.org/profile-%d/v1" prof-num)
        ?atype-iri (first (iri/create-iri-vec "activity"
                                              (:num-profiles args)
                                              (:num-activity-types args)
                                              (:max-iris args)))]
    (cond-> {:id                 id
             :inScheme           inscheme
             :type               type-str
             :activityDefinition {:name        {:en-US (format "%s %d" type-str concept-num)}
                                  :description {:en-US (format "%s Number %d" type-str concept-num)}
                                  :_context    "https://w3id.org/xapi/profiles/activity-context"}}
      ?atype-iri
      (assoc-in [:activityDefinition :type] ?atype-iri))))
