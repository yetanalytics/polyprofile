(ns com.yetanalytics.poly.profile.utils.time
  #?(:clj (:import [java.time Instant]
                   [java.time.temporal ChronoUnit])))

(defn timestamp-seq
  "Return a sequence of monotonically increasing timestamp strings."
  [num-timestamps]
  (repeatedly
   num-timestamps
   (fn []
     #?(:clj (let [inst (Instant/now)]
               (.toString (.truncatedTo inst ChronoUnit/MILLIS)))
        :cljs (let [date (js/Date.)]
                (.toISOString date))))))
