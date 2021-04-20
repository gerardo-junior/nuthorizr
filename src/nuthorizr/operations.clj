(ns nuthorizr.operations
  (:require [nuthorizr.domains.account :as account]
            [nuthorizr.domains.transaction :as transaction]))


(defn get-account-status
  "Get the account outbound data"
  []
  (if (account/init?) (select-keys (account/get-account) [:active-card
                                                          :available-limit]) nil))


(defn init-account
  "Starts the account if it is not responding"
  [account]
  (cond (account/init?) {:status false
                         :violations ["account-already-initialized"]}
        :else {:status (account/init account)
               :violations []}))



(defn execute-transaction
  "Validates and executes a transaction"
  [account transaction]
  (let [violations           (transaction/check-transaction account transaction)
        number-of-violations (count violations)
        status               (if (= number-of-violations 0) (transaction/execute-transaction account transaction) false)
        result               {:violations (if (and (= number-of-violations 0)
                                                   (not status)) ["system-unavailable"] violations)
                              :status status}] result))


(defn do-operation
  "Performs the processing of the operation"
  [operation]
  (let [account     (cond (contains? operation :account) (init-account (:account operation)))
        transaction (cond (contains? operation :transaction) (execute-transaction (account/get-account) (:transaction operation)))
        result      {:status     (or (:status account) (:status transaction))
                     :violations (distinct (concat (:violations account)
                                                   (:violations transaction)))}] result))