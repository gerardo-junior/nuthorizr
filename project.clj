(defproject nuthorizr "0.0.1-SNAPSHOT"

  :description "Authorizer with json over stdin"

  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.10.1"]]
  
  :main ^:skip-aot nuthorizr.core

  :target-path "target/%s"

  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
