(ns nuthorizr.config 
  (:require
    [clj-time.core :as time]))

(def transaction-rules {:doubled-transaction {:interval (time/minutes 2)}
                        :high-frequency      {:limit 3
                                              :interval (time/minutes 2)}})