(defproject gpt-vunds "0.1.0-SNAPSHOT"
  :description "openai examples in clojure - including streaming and embeddings. From newbie for newbies."
  :url "https://github.com/bennoloeffler/gpt-vunds"
  :license {:name "WTFPL"
            :url "http://www.wtfpl.net/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [net.clojars.wkok/openai-clojure "0.14.0"]
                 [com.hyperfiddle/rcf "20220926-202227"]
                 [metosin/malli "0.11.0"]
                 [mvxcvi/puget "1.3.4"]
                 [clj-http "3.12.3"]
                 [org.clojure/data.json "2.4.0"]
                 [org.apache.poi/poi "5.2.3"]
                 [org.apache.poi/poi-ooxml "5.2.3"]]

  :main ^:skip-aot gpt-vunds.from-file
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
