(ns com.yetanalytics.poly.profile-test
  (:require [clojure.test :refer [deftest testing is]]
            [com.yetanalytics.pan :as pan]
            [com.yetanalytics.poly.profile :as poly]
            [com.yetanalytics.poly.profile.utils.gen :refer [generate-object]]))

(deftest object-gen-test
  (testing "Generate a single"
    (testing "Verb"
      (let [verb (generate-object 0 0 0 "Verb" poly/default-args)]
        (is (nil? (pan/validate-object verb :type :concept)))
        (is (= "http://poly.profile/profile-0/v0/verb-0"
               (:id verb)))))
    (testing "ActivityType"
      (let [act-type (generate-object 0 0 0 "ActivityType" poly/default-args)]
        (is (nil? (pan/validate-object act-type :type :concept)))
        (is (= "http://poly.profile/profile-0/v0/activity-type-0"
               (:id act-type)))))
    (testing "AttachmentUsageType"
      (let [att-use-type (generate-object 0 0 0 "AttachmentUsageType" poly/default-args)]
        (is (nil? (pan/validate-object att-use-type :type :concept)))
        (is (= "http://poly.profile/profile-0/v0/attachment-usage-type-0"
               (:id att-use-type)))))
    (testing "ActivityExtension"
      (let [act-ext (generate-object 0 0 0 "ActivityExtension" poly/default-args)]
        (is (nil? (pan/validate-object act-ext :type :concept)))
        (is (= "http://poly.profile/profile-0/v0/activity-extension-0"
               (:id act-ext)))))
    (testing "ContextExtension"
      (let [ctx-ext (generate-object 0 0 0 "ContextExtension" poly/default-args)]
        (is (nil? (pan/validate-object ctx-ext :type :concept)))
        (is (= "http://poly.profile/profile-0/v0/context-extension-0"
               (:id ctx-ext)))))
    (testing "ResultExtension"
      (let [res-ext (generate-object 0 0 0 "ResultExtension" poly/default-args)]
        (is (nil? (pan/validate-object res-ext :type :concept)))
        (is (= "http://poly.profile/profile-0/v0/result-extension-0"
               (:id res-ext)))))
    (testing "StateResource"
      (let [st-res (generate-object 0 0 0 "StateResource" poly/default-args)]
        (is (nil? (pan/validate-object st-res :type :concept)))
        (is (= "http://poly.profile/profile-0/v0/state-resource-0"
               (:id st-res)))))
    (testing "AgentProfileResource"
      (let [agp-res (generate-object 0 0 0 "AgentProfileResource" poly/default-args)]
        (is (nil? (pan/validate-object agp-res :type :concept)))
        (is (= "http://poly.profile/profile-0/v0/agent-profile-resource-0"
               (:id agp-res)))))
    (testing "ActivityProfileResource"
      (let [acp-res (generate-object 0 0 0 "ActivityProfileResource" poly/default-args)]
        (is (nil? (pan/validate-object acp-res :type :concept)))
        (is (= "http://poly.profile/profile-0/v0/activity-profile-resource-0"
               (:id acp-res)))))
    (testing "Activity"
      (let [activity (generate-object 0 0 0 "Activity" poly/default-args)]
        (is (nil? (pan/validate-object activity :type :concept)))
        (is (= "http://poly.profile/profile-0/v0/activity-0"
               (:id activity)))))
    (testing "StatementTemplate"
      (let [template (generate-object 0 0 0 "StatementTemplate" poly/default-args)]
        (is (nil? (pan/validate-object template :type :template)))
        (is (= "http://poly.profile/profile-0/v0/template-0"
               (:id template)))))
    (testing "Pattern"
      (let [pattern (generate-object 0 0 0 "Pattern" poly/default-args)]
        (is (nil? (pan/validate-object pattern :type :pattern)))
        (is (= "http://poly.profile/profile-0/v0/pattern-0"
               (:id pattern)))))))

(deftest profile-gen-test
  (testing "Generate a seq of profiles"
    (let [prof-lazy-seq (poly/generate-profile-seq)]
      (is (instance? #?(:clj clojure.lang.LazySeq
                        :cljs cljs.core/LazySeq)
                     prof-lazy-seq))
      (is (= (:num-profiles poly/default-args)
             (count prof-lazy-seq)))
      (is (-> prof-lazy-seq
              vec
              ;; Don't set `:relations?` to true (despite being fairly
              ;; important) since polyprofile doesn't account for specialized
              ;; Pattern relation specs

              ;; TODO: Ensure that all Pattern relation specs are followed
              ;; (and fix any Pan bugs while at it).
              (pan/validate-profile-coll :syntax? true
                                         :ids? true
                                         :context? true
                                        ;;  :relations? true
                                         )
              nil?))))
  (testing "Generate a seq of empty profiles"
    (let [prof-lazy-seq (poly/generate-profile-seq {:num-verbs 0
                                                    :num-activity-types 0
                                                    :num-attachment-usage-types 0
                                                    :num-activities 0
                                                    :num-statement-templates 0
                                                    :num-patterns 0})
          empty-prof    (first prof-lazy-seq)]
      (is (= (:num-profiles poly/default-args)
             (count prof-lazy-seq)))
      (is (not (contains? empty-prof :concepts)))
      (is (not (contains? empty-prof :templates)))
      (is (not (contains? empty-prof :patterns)))))
  (testing "Generate an seq of profiles with no versions"
    (let [empty-prof (first (poly/generate-profile-seq {:num-profiles 1
                                                        :num-versions 0}))]
      (is (not (contains? empty-prof :concepts)))
      (is (not (contains? empty-prof :templates)))
      (is (not (contains? empty-prof :patterns)))))
  (testing "Generate an empty seq of profiles"
    (let [empty-seq (poly/generate-profile-seq {:num-profiles 0})]
      (is (empty? empty-seq)))))
