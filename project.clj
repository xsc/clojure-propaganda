(defproject playground "0.1.0-SNAPSHOT"
  :description "Clojure introduction sessions"
  :url "https://github.com/xsc/clojure-introduction"
  :license {:name "MIT"
            :url "https://choosealicense.com/licenses/mit"
            :comment "MIT License"
            :author "Yannick Scherer"
            :year 2022
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [aleph "0.4.6"]]
  :profiles {:kaocha {:dependencies [[lambdaisland/kaocha "1.62.993"
                                      :exclude [org.clojure/clojure]]]}}
  :aliases {"kaocha" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner"]})
