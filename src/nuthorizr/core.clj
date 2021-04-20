(ns nuthorizr.core
  (:gen-class)
   (:require [nuthorizr.operations :as operations]
             [cheshire.core        :as json]
             [clojure.java.io      :as io]))


(defn handler
  "Function to parser inbound, execute operation and encode outbound"
  [inbound]
  (json/encode (try (let [operation (json/decode inbound true)
                          result    (operations/do-operation operation)
                          outbound  {:account (operations/get-account-status)
                                     :violations (:violations result)}] outbound)
                (catch com.fasterxml.jackson.core.JsonParseException err 
                       {:account (operations/get-account-status)
                        :violations ["inbound-parse-failure"]}))))

(defn -main
  "Main function of app to get stdin and return to stdout operations"
  []
  (doseq [inbound (line-seq (io/reader *in*))]
         (-> inbound
             (handler)
             (println))))
