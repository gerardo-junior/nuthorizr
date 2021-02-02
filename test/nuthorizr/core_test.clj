(ns nuthorizr.core-test
  (:require [clojure.test   :refer :all]
            [nuthorizr.core :refer :all]
            [nuthorizr.domains.account :as account]
            [cheshire.core  :as json]))

(deftest test-if-the-output-of-the-handler-function-is-a-valid-json
  (testing "If the output of the handler function is a valid json"
    (account/destroy)
    (is (map? (json/decode (handler nil) true)))))


(deftest test-error-handling-if-input-json-is-not-valid
  (testing "Error handling if input json is not valid"
    (account/destroy)
    (is (.contains (:violations (json/decode (handler "{1}") true)) "inbound-parse-failure"))))


(deftest test-of-start-the-account-once-time
  (testing "Start the account once time"
    (account/destroy)
    (is (contains? (:account (json/decode (handler "{\"account\": {\"active-card\": true, \"available-limit\": 2000}}") true)) :active-card))))


(deftest test-of-start-the-account-several-times
  (testing "Start the account several times"
    (account/destroy)
    (is (.contains (:violations (json/decode (-> "{\"account\": {\"active-card\": true, \"available-limit\": 2000}}"
                                                 (handler)
                                                 (handler)) true)) "account-already-initialized"))))

(deftest test-if-the-limit-is-decrementing-with-successful-transfer
  (testing "If the limit is decrementing with successful transfer"
    (account/destroy)
    (is (= (:available-limit (:account (do (handler "{\"account\": {\"active-card\": true, \"available-limit\": 200}}")
                                           (json/decode (handler "{\"transaction\": {\"merchant\": \"Habbibs\", \"amount\": 100, \"time\":\"2020-01-01T01:01:00.000Z\"}}") true))))
           100))))

(deftest test-if-the-limit-is-decrementing-with-fail-transfer
  (testing "If the limit is decrementing with successful transfer"
    (account/destroy)
    (is (= (:available-limit (:account (do (handler "{\"account\": {\"active-card\": false, \"available-limit\": 200}}")
                                           (json/decode (handler "{\"transaction\": {\"merchant\": \"Habbibs\", \"amount\": 100, \"time\":\"2020-01-01T01:01:00.000Z\"}}") true))))
           200))))


(deftest test-make-a-transfer-without-init-account
  (testing "Make a transfer without init account"
    (account/destroy)
    (is (.contains (:violations (json/decode (handler "{\"transaction\": {\"merchant\": \"Habbibs\", \"amount\": 20, \"time\":\"2020-01-01T01:01:00.000Z\"}}") true)) "account-not-initialized"))))


(deftest test-try-to-transfer-with-disabled-card
  (testing "Try to transfer with disabled card"
    (account/destroy)
    (is (.contains (:violations (do (handler "{\"account\": {\"active-card\": false, \"available-limit\": 200}}")
                                    (json/decode (handler "{\"transaction\": {\"merchant\": \"Habbibs\", \"amount\": 1, \"time\":\"2020-01-01T01:01:00.000Z\"}}") true))) "card-not-active"))))

(deftest test-try-to-transfer-no-limit-available-on-the-account
  (testing "Try to transfer no limit available limit on the account"
    (account/destroy)
    (is (.contains (:violations (do (handler "{\"account\": {\"active-card\": true, \"available-limit\": 200}}")
                                    (json/decode (handler "{\"transaction\": {\"merchant\": \"Habbibs\", \"amount\": 10000, \"time\":\"2020-01-01T01:01:00.000Z\"}}") true))) "insufficient-limit"))))

(deftest test-make-the-same-transaction-in-the-short-period-of-time
  (testing "Make the same transaction in the short period of time"
    (account/destroy)
    (is (.contains (:violations (do (handler "{\"account\": {\"active-card\": true, \"available-limit\": 2000}}")
                                    (handler "{\"transaction\": {\"merchant\": \"Habbibs\", \"amount\": 20, \"time\":\"2020-01-01T01:01:00.000Z\"}}")
                                    (json/decode (handler "{\"transaction\": {\"merchant\": \"Habbibs\", \"amount\": 20, \"time\":\"2020-01-01T01:01:00.000Z\"}}") true))) "doubled-transaction"))))

(deftest test-make-many-transfers-in-a-short-period-of-time
  (testing "Make many transfers in a short period of time"
    (account/destroy)
    (is (.contains (:violations (do (handler "{\"account\": {\"active-card\": true, \"available-limit\": 2000}}")
                                    (handler "{\"transaction\": {\"merchant\": \"Habbibs\", \"amount\": 20, \"time\":\"2020-01-01T01:01:00.000Z\"}}")
                                    (handler "{\"transaction\": {\"merchant\": \"kalunga\", \"amount\": 20, \"time\":\"2020-01-01T01:01:00.000Z\"}}")
                                    (handler "{\"transaction\": {\"merchant\": \"centauro\", \"amount\": 20, \"time\":\"2020-01-01T01:01:00.000Z\"}}")
                                    (handler "{\"transaction\": {\"merchant\": \"vivara\", \"amount\": 20, \"time\":\"2020-01-01T01:01:00.000Z\"}}")
                                    (json/decode (handler "{\"transaction\": {\"merchant\": \"mc\", \"amount\": 20, \"time\":\"2020-01-01T01:01:00.000Z\"}}") true))) "high-frequency-small-interval"))))