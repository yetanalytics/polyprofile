(ns com.yetanalytics.poly.profile.utils.gen)

(defmulti generate-object
  "Generate a single object, e.g. a Concept, StatementTemplate, or Pattern.
   The object will have an ID of the form
   ```
   http://poly.profile/profile-[prof-num]/object-[obj-num]
   ```
   and will include generated IRIs, whose generation will be determined by
   the current numerical IDs and the max numbers of profiles and objects."
  {:arglists '([prof-number
                ver-number
                obj-number
                obj-type
                args])}
  (fn [_ _ _ obj-type _] obj-type))
