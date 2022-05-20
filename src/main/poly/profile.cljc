(ns poly.profile
  (:require [poly.profile.concept    :refer [generate-concepts]]
            [poly.profile.template   :refer [generate-templates]]
            [poly.profile.pattern    :refer [generate-patterns]]
            [poly.profile.utils.iri  :as iri]
            [poly.profile.utils.time :as t]))

(def default-args
  {:num-profiles                   10
   :num-versions                   2
   :num-verbs                      5
   :num-activity-types             5
   :num-attachment-usage-types     2
   :num-activity-extensions        0
   :num-context-extensions         0
   :num-result-extensions          0
   :num-state-resources            0
   :num-activity-profile-resources 0
   :num-agent-profile-resources    0
   :num-activities                 1
   :num-statement-templates        5
   :num-patterns                   5
   :max-iris                       5})

(defn- generate-version-vector
  "Generate the Profile `versions` vector."
  [profile-num num-versions]
  (let [id-coll  (map (partial iri/create-iri profile-num)
                      (range num-versions))
        ts-coll  (t/timestamp-seq num-versions)]
    (mapv (fn [id ts] {:id id :generatedAtTime ts})
          id-coll
          ts-coll)))

(defn- generate-profile-info
  "Generate the Profile top-level metadata, including `author` and `versions`."
  [profile-num {:keys [num-versions]}]
  {:id         (iri/create-iri profile-num)
   :type       "Profile"
   :_context   "https://w3id.org/xapi/profiles/context"
   :conformsTo "https://w3id.org/xapi/profiles#1.0"
   :prefLabel  {:en-US (str "Profile " profile-num)}
   :definition {:en-US (str "Generated Profile Number " profile-num)}
   :author     {:type "Organization"
                :name "Yet Analytics"}
   :versions   (generate-version-vector profile-num num-versions)})

(defn- generate-version-objects
  "Generate the `concepts`, `templates`, and `patterns` vectors of a
   single Profile version."
  [profile-num version-num args]
  (let [concepts  (generate-concepts profile-num version-num args)
        templates (generate-templates profile-num version-num args)
        patterns  (generate-patterns profile-num version-num args)]
    {:concepts  concepts
     :templates templates
     :patterns  patterns}))

(defn- generate-profile-objects
  "Generate the `concepts`, `templates`, and `patterns` vectors of a
   single Profile."
  [profile-num {:keys [num-versions] :as args}]
  (let [{:keys [concepts templates patterns]}
        (->> (range num-versions)
             (map (fn [ver-num]
                    (generate-version-objects profile-num ver-num args)))
             (reduce (partial merge-with concat) {}))]
    (cond-> {}
      (not-empty concepts)
      (assoc :concepts (vec concepts))
      (not-empty templates)
      (assoc :templates (vec templates))
      (not-empty patterns)
      (assoc :patterns (vec patterns)))))

(defn- generate-profile
  "Generate a single Profile."
  [profile-num args]
  (merge (generate-profile-info profile-num args)
         (generate-profile-objects profile-num args)))

(defn generate-profile-seq
  "Generate a lazy sequence of Profiles. See `default-args` for allowed args;
   if `args` is not passed then `default-args` is used."
  ([]
   (generate-profile-seq {}))
  ([args]
   (let [args* (merge default-args args)
         {:keys [num-profiles]} args*]
     (map (fn [profile-num]
            (generate-profile profile-num args*))
          (range num-profiles)))))
