(ns german-bank-csv-to-ledger-cli.core-test
  (:require [clojure.test :refer :all]
            [german-bank-csv-to-ledger-cli.core :refer :all :as mygerman]))


(deftest test-convert-to-ledger-format-with-betreff
 (testing "FIXME, betreff")
  (is (= (convert-to-ledger-format ["30.12.2021" "30.12.2021" "Kartenzahlung" "" "NETTO MARKEN-DISCOU//DUESSELDORF/DE 29-12-2021T21:34:38 Folgenr. 07 Verfalld. 1523"  "" "" "" "-20,20"  "EUR"])
"2021/12/30 * NETTO MARKEN-DISCOU//DUESSELDORF/DE 29-12-2021T21:34:38 Folgenr. 07 Verfalld. 1523
	Assets:Food:Supermarket  €20,20
	Assets:Bank:Checking  -€20,20\n"
      )
  )
)


(deftest test-convert-to-ledger-format-with-paypal
 (testing "FIXME, paypal")
  (is (= (convert-to-ledger-format ["29.12.2021" "29.12.2021" "SEPA-Lastschrift von" "PayPal (Europe) S.a.r.l. et Cie., S.C.A." ". Spotify Limited, Ihr Einkauf bei Spotify Limited" "DE88500700102175526303" "DEUTDEFHXXX" "1017624592854  PAYPAL" "5QKJ223MTLWAL" "LU96ZZZ0000000000000000058" "" "" "" "" "" "-9,99" "" "EUR"])
"2021/12/29 *  Spotify Limited
	Expenses:Leisure:Digital:Music  €9,99
	Assets:Bank:Checking  -€9,99\n"

      )
)
)
;

(deftest test-convert-to-ledger-format-with-auftraggeber
 (testing "FIXME, aufftraggeber")
  (is (= (convert-to-ledger-format ["10.11.2021" "10.11.2021" "SEPA-Lastschrift von" "Drillisch Online GmbH" "C4413004 U440695502 B514217508 handyvertrag.de" "DE89506400151233322700" "COBADEFF506" "" "EF958053341760F03563C0DC35C" "DE40ZZZ10000206926" "" "" "" "" "" "-10,59" "" "EUR"])
"2021/11/10 * Drillisch Online GmbH
	Expenses:Leisure:Digital:Handy  €10,59
	Assets:Bank:Checking  -€10,59\n"
      )
  )
)


;30.12.2021;30.12.2021;"Kartenzahlung";;ALNATURA FIL. 118//DUESSELDORF/DE 29-12-2021T09:28:31 Folgenr. 07 Verfalld. 1223;;;;;;;;;;;-1,14;;EUR
