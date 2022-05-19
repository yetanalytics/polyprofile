(ns poly.profile.pattern
  (:require [poly.profile.utils.gen :refer [generate-object]]
            [poly.profile.utils.iri :as iri]
            #?@(:cljs [[goog.string :refer [format]]
                       [goog.string.format]])))


(defn- generate-pattern-iri-scalar
  [{pat-num  :pattern-number
    num-temp :num-statement-templates
    num-pat  :num-patterns
    :as      args}]
  (if (= 0 (rand-int 2))
    (first (iri/create-iri-vec (assoc args
                                      :component-slug "template"
                                      :num-components num-temp
                                      :max-iris 1)))
    (first (iri/create-iri-vec (assoc args
                                      :component-slug "pattern"
                                      :num-components num-pat
                                      :component-number pat-num
                                      :max-iris 1)))))

(defn- generate-pattern-iri-coll
  [{pat-num  :pattern-number
    num-temp :num-statement-templates
    num-pat  :num-patterns
    max-iris :max-iris
    :as      args}]
  (-> (concat
       (iri/create-iri-vec (assoc args
                                  :component-slug "template"
                                  :num-components num-temp
                                  :max-iris (/ max-iris 2)))
       (iri/create-iri-vec (assoc args
                                  :component-slug "pattern"
                                  :num-components num-pat
                                  :component-number pat-num
                                  :max-iris (/ max-iris 2))))
      shuffle))

(defmethod generate-object "Pattern"
  [{prof-num  :profile-number
    pat-num   :component-number
    type-str  :component-type
    :as       args}]
  (let [id       (format "http://example.org/profile-%d/pattern-%d" prof-num pat-num)
        inscheme (format "http://example.org/profile-%d/v1" prof-num)
        pat-kw   (rand-nth [:sequence :alternates :optional :oneOrMore :zeroOrMore])
        iris     (condp #(contains? %1 %2) pat-kw
                   #{:optional :oneOrMore :zeroOrMore}
                   (loop []
                     (if-some [iri (generate-pattern-iri-scalar args)]
                       iri
                       (recur)))
                   #{:sequence :alternates}
                   (loop []
                     (let [?iris (generate-pattern-iri-coll args)]
                       (if (and ?iris (<= 2 (count ?iris)))
                         ?iris
                         (recur)))))]
    {:id         id
     :inScheme   inscheme
     :type       type-str
     :prefLabel  {:en-US (format "Pattern %d" pat-num)}
     :definition {:en-US (format "Pattern Number %d" pat-num)}
     pat-kw      iris}))

(defn generate-patterns
  [{:keys [num-patterns] :as args}]
  (not-empty
   (mapv (fn [idx]
           (generate-object (assoc args
                                   :component-number idx
                                   :component-type "Pattern")))
         (range num-patterns))))

