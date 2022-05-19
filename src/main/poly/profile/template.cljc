(ns poly.profile.template
  (:require [poly.profile.utils.gen :refer [generate-object]]
            [poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(defmethod generate-object "StatementTemplate"
  [prof-num
   template-num
   template-type
   {num-profs     :num-profiles
    num-verbs     :num-verbs
    num-act-types :num-activity-types
    num-att-types :num-attachment-usage-types
    num-templates :num-statement-templates
    max-iris      :max-iris}]
  (let [vrb-slug "verb"
        act-slug "activity-type"
        att-slug "attachment-usage-type"
        tmp-slug "template"
        id       (format "http://example.org/profile-%d/template-%d" prof-num template-num)
        inscheme (format "http://example.org/profile-%d/v1" prof-num)
        ?verb    (first (iri/create-iri-vec vrb-slug num-profs num-verbs 1))
        ?oat     (first (iri/create-iri-vec act-slug num-profs num-act-types 1))
        ?ccat    (iri/create-iri-vec act-slug num-profs num-act-types max-iris)
        ?cgat    (iri/create-iri-vec act-slug num-profs num-act-types max-iris)
        ?cpat    (iri/create-iri-vec act-slug num-profs num-act-types max-iris)
        ?coat    (iri/create-iri-vec act-slug num-profs num-act-types max-iris)
        ?aut     (iri/create-iri-vec att-slug num-profs num-att-types max-iris)
        ?csrt    (iri/create-iri-vec tmp-slug num-profs num-templates max-iris)
        ?osrt    (when-not ?oat
                   (iri/create-iri-vec tmp-slug num-profs num-templates max-iris))]
    (cond-> {:id         id
             :inScheme   inscheme
             :type       template-type
             :prefLabel  {:en-US (format "Statement Template %d" template-num)}
             :definition {:en-US (format "Statement Template Number %d" template-num)}}
      ?verb (assoc :verb ?verb)
      ?oat  (assoc :objectActivityType ?oat)
      ?ccat (assoc :contextCategoryActivityType ?ccat)
      ?cgat (assoc :contextGroupingActivityType ?cgat)
      ?cpat (assoc :contextParentActivityType ?cpat)
      ?coat (assoc :contextOtherActivityType ?coat)
      ?aut  (assoc :attachmentUsageType ?aut)
      ?csrt (assoc :contextStatementRefTemplate ?csrt)
      ?osrt (assoc :objectStatementRefTemplate ?osrt))))

(defn generate-templates
  [profile-num {:keys [num-statement-templates] :as args}]
  (not-empty
   (mapv (fn [template-num]
           (generate-object profile-num
                            template-num
                            "StatementTemplate"
                            args))
         (range num-statement-templates))))
