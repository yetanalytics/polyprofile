(ns poly.profile.template
  (:require [poly.profile.utils.gen :refer [generate-object]]
            [poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(defmethod generate-object "StatementTemplate"
  [{prof-num   :profile-number
    temp-num   :component-number
    num-verbs  :num-verbs
    num-actype :num-activity-types
    num-attype :num-attachment-usage-types
    num-temp   :num-statement-templates
    type-str   :component-type
    :as        args}]
  (let [args-v   (assoc args
                        :num-components num-verbs
                        :component-slug "verb"
                        :max-iris 1)
        args-ats (assoc args
                        :num-components num-actype
                        :component-slug "activity-type"
                        :max-iris 1)
        args-act (assoc args
                        :num-components num-actype
                        :component-slug "activity-type")
        args-aut (assoc args
                        :num-components num-attype
                        :component-slug "attachment-usage-type")
        args-tmp (assoc args
                        :num-components num-temp
                        :component-slug "template")
        id       (format "http://example.org/profile-%d/template-%d" prof-num temp-num)
        inscheme (format "http://example.org/profile-%d/v1" prof-num)
        ?verb    (-> args-v iri/create-iri-vec first)
        ?oat     (-> args-ats iri/create-iri-vec first)
        ?ccat    (-> args-act iri/create-iri-vec)
        ?cgat    (-> args-act iri/create-iri-vec)
        ?cpat    (-> args-act iri/create-iri-vec)
        ?coat    (-> args-act iri/create-iri-vec)
        ?aut     (-> args-aut iri/create-iri-vec)
        ?csrt    (-> args-tmp iri/create-same-prof-iri-vec)
        ?osrt    (when-not ?oat (-> args-tmp iri/create-same-prof-iri-vec))]
    (cond-> {:id         id
             :inScheme   inscheme
             :type       type-str
             :prefLabel  {:en-US (format "Statement Template %d" temp-num)}
             :definition {:en-US (format "Statement Template Number %d" temp-num)}}
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
  [{:keys [num-statement-templates] :as args}]
  (not-empty
   (mapv (fn [idx]
           (generate-object (assoc args
                                   :component-number idx
                                   :component-type "StatementTemplate")))
         (range num-statement-templates))))
