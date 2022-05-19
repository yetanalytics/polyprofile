(ns poly.profile
  (:require [poly.profile.concept  :refer [generate-concepts]]
            [poly.profile.template :refer [generate-templates]]
            [poly.profile.pattern  :refer [generate-patterns]]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))

(def default-args
  {:num-profiles                   10
   :num-versions                   2
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
  [profile-num version-num args]
  (let [args*      (merge default-args args)
        ?concepts  (generate-concepts profile-num version-num args*)
        ?templates (generate-templates profile-num version-num args*)
        ?patterns  (generate-patterns profile-num version-num args*)]
    (cond-> {:id         (format "http://example.org/profile-%d/" profile-num)
             :type       "Profile"
             :_context   "https://w3id.org/xapi/profiles/context"
             :conformsTo "https://w3id.org/xapi/profiles#1.0"
             :prefLabel  {:en-US (str "Profile " profile-num)}
             :definition {:en-US (str "Generated Profile Number " profile-num)}
             :versions   [{:id              (format "http://example.org/profile-%d/v1" profile-num)
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
  "Generate a lazy sequence of Profiles. See `default-args` for allowed args."
  [args]
  (let [args* (merge default-args args)
        {:keys [num-profiles num-versions]} args*]
    (mapcat (fn [profile-num]
              (map (fn [version-num]
                     (generate-profile profile-num version-num args*))
                   (range num-versions)))
            (range num-profiles))))
