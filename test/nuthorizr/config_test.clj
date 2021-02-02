(ns nuthorizr.config-test
  (:require [clojure.test :refer [deftest testing is]]
            [nuthorizr.config :refer :all]))


(deftest test-if-max-time-of-similar-transactions-config-is-time-instance
  (testing "Testing whether the max time setting for similar transactions is an instance of time"
    (is (instance? org.joda.time.ReadablePeriod (:interval (:doubled-transaction transaction-rules))))))

(deftest test-if-interval-of-transactions-config-is-time-instance
  (testing "Test whether the transaction interval configuration is a time instance"
    (is (instance? org.joda.time.ReadablePeriod (:interval (:high-frequency transaction-rules))))))

(deftest test-if-limit-of-interval-transaction-config-is-positive-integer
  (testing "Test whether the limit number config in the transaction interval is a positive integer"
    (is (pos-int? (:limit (:high-frequency transaction-rules))))))