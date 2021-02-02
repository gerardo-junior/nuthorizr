(ns nuthorizr.domains.transaction-test
  (:require [clojure.test                  :refer :all]
            [nuthorizr.domains.transaction :refer :all]
            [nuthorizr.domains.account     :as account]
            [clj-time.core                 :as time]))

(deftest test-filter-by-interval-of-transactions
  (testing "Filter interval of transactions"
    (is (= (count (get-transaction-on-interval [{:merchant "mc"
                                                 :amount 3000
                                                 :time "2020-01-01T00:00:00.000Z"}
                                                {:merchant "centauro"
                                                 :amount 1000
                                                 :time "2020-01-01T01:03:00.000Z"}
                                                {:merchant "kalunga"
                                                 :amount 1000
                                                 :time "2020-01-01T01:04:00.000Z"}]
                                               {:merchant "outback"
                                                :amount 1000
                                                :time "2020-01-01T01:04:00.000Z"}
                                               (time/minutes 2))) 2))))


(deftest test-if-the-transfer-frequency-verification-function
  (testing "If the transfer frequency check"
    (is (high-frequency? {:merchant "outback"
                          :amount 1000
                          :time "2020-01-01T01:01:00.000Z"}
                         [{:merchant "mc"
                           :amount 3000
                           :time "2020-01-01T01:01:00.000Z"}
                          {:merchant "centauro"
                           :amount 1000
                           :time "2020-01-01T01:01:00.000Z"}
                          {:merchant "kalunga"
                           :amount 1000
                           :time "2020-01-01T01:01:00.000Z"}]))))


(deftest test-double-transfer-verification-function
  (testing "Double transfer verification function"
    (is (doubled-transaction? {:merchant "outback"
                               :amount 1000
                               :time "2020-01-01T01:01:00.000Z"}
                              [{:merchant "outback"
                                :amount 1000
                                :time "2020-01-01T01:01:00.000Z"}]))))

(deftest test-transfer-validation-function-no-account-initialized
  (testing "Transfer validation function no account initialized"
    (account/destroy)
    (is (.contains (let [account nil
                         result (check-transaction account
                                                   {:merchant "outback"
                                                    :amount 1000
                                                    :time "2020-01-01T01:01:00.000Z"})] result) "account-not-initialized"))))


(deftest test-transfer-validation-function-with-disable-card
  (testing "Transfer validation function with disable card"
    (account/destroy)
    (is (.contains (let [account (do (account/init {:active-card false
                                                    :available-limit 2000})
                                     (account/get-account))
                         result (check-transaction account
                                                   {:merchant "outback"
                                                    :amount 1000
                                                    :time "2020-01-01T01:01:00.000Z"})] result) "card-not-active"))))


(deftest test-transfer-validation-function-no-limit-available
  (testing "Transfer validation function no limit available"
    (account/destroy)
    (is (.contains (let [account (do (account/init {:active-card true
                                                    :available-limit 200})
                                     (account/get-account))
                         result (check-transaction account
                                                   {:merchant "outback"
                                                    :amount 1000
                                                    :time "2020-01-01T01:01:00.000Z"})] result) "insufficient-limit"))))