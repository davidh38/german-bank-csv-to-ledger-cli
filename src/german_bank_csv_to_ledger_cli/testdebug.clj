(ns german-bank-csv-to-ledger-cli.testdebug)
(defn myfunction2 []
  (println "hy"))


(defn myfunction []
(println "hi")
#break
(myfunction2 "ho")
)


(myfunction
            
            
            )