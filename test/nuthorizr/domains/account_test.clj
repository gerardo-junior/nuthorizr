(ns nuthorizr.domains.account-test
  (:require [clojure.test              :refer :all]
            [nuthorizr.domains.account :refer :all]))


(deftest test-if-atom-is-being-defined
  (testing "If atom is being defined"
    (is (instance? clojure.lang.Atom account-data))))


(deftest test-verification-function-without-instantiating
  (testing "Verification function without instantiating"
    (destroy)
    (is (= false (init?)))))


(deftest test-if-start-function-instates-a-new-account
  (testing "If start function instates a new account"
    (destroy)
    (init {:active-card false
           :available-limit 2000})
    (is (init?))))

(deftest test-check-if-destruction-of-state-function-is-working-correctly
  (testing "Check if destruction of state function is working correctly"
    (init {:active-card false
           :available-limit 2000})
    (destroy)
    (is (= false (init?)))))

(deftest test-check-get-account-function
  (testing "Check get account function"
    (destroy)
    (init {:active-card true
           :available-limit 2000})
    (is (= 2000 (:available-limit (get-account))))))

(deftest test-check-the-card-limit-test-function
  (testing "Check the card limit test function"
    (destroy)
    (init {:active-card true
           :available-limit 20})
    (is (has-limit? (get-account) 10))))


(deftest test-check-the-get-history-function
  (testing "Check the get history function"
    (destroy)
    (init {:active-card true
           :available-limit 20})
    (is (= clojure.lang.PersistentVector (type (get-transaction-history (get-account)))))))

(deftest test-check-add-transaction-function
  (testing "Check the add transaction function"
    (destroy)
    (init {:active-card true
           :available-limit 20})
    (is (add-transaction (get-account) {:merchant "outback"
                                        :amount 1000
                                        :time "2020-01-01T01:01:00.000Z"}))))

