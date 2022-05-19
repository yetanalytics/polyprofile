(ns poly.profile
  (:require [poly.profile.concept  :refer [generate-concepts]]
            [poly.profile.template :refer [generate-templates]]
            [poly.profile.pattern  :refer [generate-patterns]]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(def default-args
  {:num-profiles                   10
   :num-verbs                      5
   :num-activity-types             5
   :num-attachment-usage-types     2
   :num-activity-extensions        1
   :num-context-extensions         1
   :num-result-extensions          1
   :num-activity-profile-resources 1
   :num-activities                 1
   :num-statement-templates        5
   :num-patterns                   5
   :max-iris                       5})

(defn- generate-profile
  [{prof-num :profile-number
    :as      args}]
  (let [?concepts  (generate-concepts args)
        ?templates (generate-templates args)
        ?patterns  (generate-patterns args)]
    (cond-> {:id         (format "http://example.org/profile-%d/" prof-num)
             :type       "Profile"
             :_context   "https://w3id.org/xapi/profiles/context"
             :conformsTo "https://w3id.org/xapi/profiles#1.0"
             :prefLabel  {:en-US (str "Profile " prof-num)}
             :definition {:en-US (str "Generated Profile Number " prof-num)}
             :versions   [{:id              (format "http://example.org/profile-%d/v1" prof-num)
                           :generatedAtTime #?(:clj (let [inst  (java.time.Instant/now)
                                                          nanos (.getNano inst)]
                                                      (.toString (.minusNanos inst nanos)))
                                               :cljs (.toString (js/now)))}]
             :author     {:type "Organization"
                          :name "Yet Analytics"}}
      ?concepts  (assoc :concepts ?concepts)
      ?templates (assoc :templates ?templates)
      ?patterns  (assoc :patterns ?patterns))))

(defn generate-profile-seq
  "Generate a lazy sequence of Profiles."
  [{:keys [num-profiles]
    :as args}]
  (let [args* (merge default-args args)]
    (map (fn [prof-num] (generate-profile (assoc args* :profile-number prof-num)))
         (range num-profiles))))
