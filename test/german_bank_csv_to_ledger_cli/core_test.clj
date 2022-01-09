(ns german-bank-csv-to-ledger-cli.core-test
  (:require [clojure.test :refer :all]
            [german-bank-csv-to-ledger-cli.core :refer :all :as mygerman]))


(deftest test-convert-to-ledger-format
 (testing "FIXME, I fail")
  (is (= (convert-to-ledger-format ["30.12.2021" "30.12.2021" "Kartenzahlung" "" "NETTO MARKEN-DISCOU//DUESSELDORF/DE 29-12-2021T21:34:38 Folgenr. 07 Verfalld. 1223"  "" "" "" "-20,20"  "EUR"])
"2021/12/30 * NETTO MARKEN-DISCOU//DUESSELDORF/DE 29-12-2021T21:34:38 Folgenr. 07 Verfalld. 1223
	Assets:Food:Supermarket  €20,20
	Assets:Bank:Checking  -€20,20\n"
      )
  )
)


;30.12.2021;30.12.2021;"Kartenzahlung";;ALNATURA FIL. 118//DUESSELDORF/DE 29-12-2021T09:28:31 Folgenr. 07 Verfalld. 1223;;;;;;;;;;;-1,14;;EUR
