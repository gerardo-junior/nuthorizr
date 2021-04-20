(ns nuthorizr.domains.account)

(defrecord Account [active-card available-limit transactions])

(def account-data (atom {}))

(defn destroy
  "Destroy the account state"
  [] 
  (reset! account-data {}))

(defn init
  "Initializes an account"
  [account]
  (try (do (reset! account-data (Account. (:active-card account)
                                     (:available-limit account)
                                     [])) true)
       (catch Exception e false)))

(defn init?
  "Checks if the account has already been initialized"
  []
  (= (type @account-data) Account))

(defn get-account
  "Get the initialized account"
  []
  (if (init?) @account-data nil))

(defn card-available?
  "Check if the card is active"
  [account]
  (:active-card account))

(defn has-limit?
  "Check if there is a limit available"
  [account amount]
  (>= (:available-limit account) amount))

(defn get-transaction-history
  "Get the history of transactions carried out"
  [account]
  (:transactions account))

(defn add-transaction
  "Add a transaction to the history and subtract the limit"
  [account transaction]
  (try (do (reset! account-data (Account. (:active-card account) 
                                 (- (:available-limit account) (:amount transaction))
                                 (conj (:transactions account) transaction))) true)
       (catch Exception e false)))
