(defproject nuthorizr "0.0.1-SNAPSHOT"

  :description "A Authorizer app with json over stdin"

  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [cheshire "5.6.1"]
                 [clj-time "0.15.2"]]
  
  :main ^:skip-aot nuthorizr.core

  :target-path "target/%s"

  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
              
             :dev [{:plugins [[jonase/eastwood "0.3.13"]]
                    :eastwood {:exclude-linters [:constant-test]
                               :include-linters [:deprecations]}}]})
