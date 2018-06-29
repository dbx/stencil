(defproject io.github.erdos/stencil "0.1.8-SNAPSHOT"
  :description       "Templating engine for office documents."
  :min-lein-version  "2.0.0"
  :java-source-paths ["java-src"]
  :javac-options     ["-target" "8" "-source" "8"]
  :aot               :all
  :dependencies [;[org.apache.maven.surefire/surefire-providers "2.22.0"]
                 [com.taoensso/timbre "4.10.0"] ;; naplozas
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.xml "0.2.0-alpha5"]
                 [org.jodconverter/jodconverter-local "4.1.0"]] ;; pdf conversion
  :pom-addition ([:properties ["maven.compiler.source" "8"] ["maven.compiler.target" "8"]])
  :pom-plugins [[org.apache.maven.plugins/maven-surefire-plugin "2.22.0"]]

  :plugins [[quickie "0.4.2" :exclusions [org.clojure/clojure]]
            [lein-javadoc "0.3.0"]
            [lein-test-out "0.3.1"]]
  :aliases      {"junit" ["with-profile" "test" "do" "test-out" "junit" "junit.xml"]}
  :javadoc-opts {:package-names ["io.github.erdos.stencil"]
                 :additional-args ["-overview" "java-src/overview.html"
                                   "-top" "<style>kbd{background:#ddd}; a[title~=class], a[title~=interface], a[title~=enum]{text-decoration: underline; font-weight: bold} dd>code{background:#eee}</style>"]}
  :main io.github.erdos.stencil.standalone.Main
  :repl-options {:init-ns stencil.process}
  :jar-exclusions [#".*\.xml"]
  :profiles {:test {:dependencies [[junit/junit "4.12"]
                                   [org.xmlunit/xmlunit-core "2.5.1"]
                                   [hiccup "1.0.5"]]
                    :resource-paths    ["test-resources"]
                    :java-source-paths ["java-src" "test"]}})
