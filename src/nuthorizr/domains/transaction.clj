(ns nuthorizr.domains.transaction
  (:require [nuthorizr.domains.account :as account]
            [nuthorizr.config          :as config]
            [clj-time.core             :as t]
            [clj-time.coerce           :as tp]))


(defn get-transaction-on-interval
  "Filter a list to get only transaction on certain interval of time"
  [list-of-transactions transaction interval]
  (filter (fn [from-history] (>= (t/in-millis interval)
                                 (t/in-millis (let [time-vector   (sort [(tp/from-string (:time from-history))
                                                                         (tp/from-string (:time transaction))])
                                                    time-interval (t/interval (first time-vector)
                                                                              (last time-vector))] time-interval)))) list-of-transactions))

(defn high-frequency?
  "Checks if there are a number of transactions within a certain interval of time"
  [transaction history]
  (let [rules       (:high-frequency config/transaction-rules)
        on-interval (get-transaction-on-interval history transaction (:interval rules))
        result      (>= (count on-interval) (:limit rules))] result))


(defn doubled-transaction?
  "Checks for duplicate transactions within a certain interval of time"
  [transaction history]
  (let [rules               (:doubled-transaction config/transaction-rules)
        on-interval         (get-transaction-on-interval history transaction (:interval rules))
        doubled-transaction (filter (fn [from-history] (and (= (:merchant transaction) (:merchant from-history))
                                                            (= (:amount transaction) (:amount from-history)))) on-interval)
        result              (> (count doubled-transaction) 0)] result))


(defn check-transaction
  "Checklist to see if a transaction can happen"
  [account transaction]
  (cond (not (account/init?)) ["account-not-initialized"]
        (not (account/card-available? account)) ["card-not-active"]
        :else (let [available-limit     (when-not (account/has-limit? account (:amount transaction)) ["insufficient-limit"])
                    history             (account/get-transaction-history account)
                    high-frequency      (when (high-frequency? transaction history) ["high-frequency-small-interval"])
                    doubled-transaction (when (doubled-transaction? transaction history) ["doubled-transaction"])
                    violations          (distinct (concat available-limit
                                                          high-frequency
                                                          doubled-transaction))] violations)))

(defn execute-transaction
  "Executa uma transação adicionado um transação na conta"
  [account transaction]
  (account/add-transaction account transaction))