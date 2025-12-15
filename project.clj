(defproject german-bank-csv-to-ledger-cli "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clojure-csv "2.0.2"]
                 [ring/ring-core "1.12.2"]
                 [hiccup/hiccup "2.0.0-alpha2"]
                 [ring/ring-jetty-adapter "1.12.2"]
                 [ring-refresh "0.2.0"]
                 [ring/ring-devel "1.12.2"]
                 [djblue/portal "0.50.0"]]
  :main ^:skip-aot german-bank-csv-to-ledger-cli.io
  :target-path "target/%s"
  :plugins [[cider/cider-nrepl "0.55.7"]])
