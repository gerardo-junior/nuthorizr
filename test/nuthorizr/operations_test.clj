(ns nuthorizr.operations-test
  (:require [clojure.test              :refer :all]
            [nuthorizr.operations      :refer :all]
            [nuthorizr.domains.account :as account]))


(deftest test-if-starts-account
  (testing "if starts account"
    (account/destroy)
    (init-account {:active-card true :available-limit 2000})
    (is (account/init?))))

(deftest test-to-get-the-account-status
  (testing "test to get the account status"
    (account/destroy)
    (init-account {:active-card true :available-limit 2000})
    (is (let [account (get-account-status)
              result (and (contains? account :active-card) (contains? account :available-limit))] result))))