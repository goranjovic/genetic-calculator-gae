(defproject gennum "1.0.0-SNAPSHOT"
  :description "Genetic my Number"
  :namespaces [gennum.core]
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.0"]
                 [hiccup "0.3.4"]
                 [ring/ring-jetty-adapter "0.3.7"]]
  :dev-dependencies [[lein-eclipse "1.0.0"]
                     [appengine-magic "0.4.0"]]
  ;:compile-path "war/WEB-INF/classes"
  ;:library-path "war/WEB-INF/lib"
  )
