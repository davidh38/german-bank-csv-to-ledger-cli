(ns german-bank-csv-to-ledger-cli.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [german-bank-csv-to-ledger-cli.conf :as myconf]
            [german-bank-csv-to-ledger-cli.core :refer [convert-to-ledger-format determine-recipient]]))

(deftest test-convert-to-ledger-format2-with-betreff
  (testing "FIXME, betreff")
  (is (= "2021/12/30 * NETTO MARKEN-DISCOU//DUESSELDORF/DE 29-12-2021T21:34:38 Folgenr. 07 Verfalld. 1523
\tAssets:Food:Supermarket  20,20 EUR
\tAssets:Bank:Checking  -20,20 EUR\n"
(convert-to-ledger-format myconf/recipient-to-moneycategory ["12/30/2021" "12/30/2021" "Debit Card Payment" "ABRECHNUNG KARTE" "NETTO MARKEN-DISCOU//DUESSELDORF/DE 29-12-2021T21:34:38 Folgenr. 07 Verfalld. 1523"  "" "" "" "-20,20"  "EUR"])
      )    )
  )

(deftest test-convert-to-ledger-format-with-betreff
  (testing "FIXME, betreff")
  (is (= (convert-to-ledger-format myconf/recipient-to-moneycategory ["12/30/2021" "12/30/2021" "Debit Card Payment" "ABRECHNUNG KARTE" "NETTO MARKEN-DISCOU//DUESSELDORF/DE 29-12-2021T21:34:38 Folgenr. 07 Verfalld. 1523"  "" "" "" "-20,20"  "EUR"])
        "2021/12/30 * NETTO MARKEN-DISCOU//DUESSELDORF/DE 29-12-2021T21:34:38 Folgenr. 07 Verfalld. 1523
	Assets:Food:Supermarket  20,20 EUR
	Assets:Bank:Checking  -20,20 EUR\n")))

(deftest test-convert-to-ledger-format-with-paypal
  (testing "FIXME, paypal")
  (is 
   (= 
    (convert-to-ledger-format myconf/recipient-to-moneycategory ["12/29/2021" "12/29/2021" "SEPA Direct Debit" 
                                                                 "PayPal Europe S.a.r.l. et Cie S.C.A" ". Spotify Limited, Ihr Einkauf bei Spotify Limited" "DE88500700102175526303" "DEUTDEFHXXX" "1017624592854  PAYPAL" "5QKJ223MTLWAL" "LU96ZZZ0000000000000000058" "" "" "" "" "" "-9,99" "" "EUR"])
                                                                  
         "2021/12/29 * . Spotify Limited, Ihr Einkauf bei Spotify Limited
\tExpenses:Leisure:Digital:Music  9,99 EUR
\tAssets:Bank:Checking  -9,99 EUR\n"))
  )
;

(deftest test-convert-to-ledger-format-with-auftraggeber
  (testing "FIXME, aufftraggeber")
  (is (= (convert-to-ledger-format myconf/recipient-to-moneycategory ["11/10/2021" "11/10/2021" "SEPA-Lastschrift von" "Drillisch Online GmbH" "C4413004 U440695502 B514217508 handyvertrag.de" "DE89506400151233322700" "COBADEFF506" "" "EF958053341760F03563C0DC35C" "DE40ZZZ10000206926" "" "" "" "" "" "-10,59" "" "EUR"])
         "2021/11/10 * Drillisch Online GmbH
	Expenses:Leisure:Digital:Handy  10,59 EUR
	Assets:Bank:Checking  -10,59 EUR\n")))

(deftest test-determine-recipient-with-auftraggeber
  (testing "FIXME, determine-recipient")
  (is (= (determine-recipient ["10.11.2021" "10.11.2021" "SEPA-Lastschrift von" "Drillisch Online GmbH" "C4413004 U440695502 B514217508 handyvertrag.de" "DE89506400151233322700" "COBADEFF506" "" "EF958053341760F03563C0DC35C" "DE40ZZZ10000206926" "" "" "" "" "" "-10,59" "" "EUR"])
         "Drillisch Online GmbH")))

(deftest test-determine-recipient-with-betreff
  (testing "FIXME, determine-recipient-betreff")
  (is (= (determine-recipient ["30.12.2021" "30.12.2021" "Kartenzahlung" "" "NETTO MARKEN-DISCOU//DUESSELDORF/DE 29-12-2021T21:34:38 Folgenr. 07 Verfalld. 1523"  "" "" "" "-20,20"  "EUR"])
         "NETTO MARKEN-DISCOU//DUESSELDORF/DE 29-12-2021T21:34:38 Folgenr. 07 Verfalld. 1523")))

(deftest test-determine-recipient-with-paypal
  (testing "FIXME, determine-recipient-paypal")
  (is (= (determine-recipient ["29.12.2021" "29.12.2021" "SEPA Direct Debit" "PayPal Europe S.a.r.l. et Cie S.C.A" ". Spotify Limited, Ihr Einkauf bei Spotify Limited" "DE88500700102175526303" "DEUTDEFHXXX" "1017624592854  PAYPAL" "5QKJ223MTLWAL" "LU96ZZZ0000000000000000058" "" "" "" "" "" "-9,99" "" "EUR"])
         ". Spotify Limited, Ihr Einkauf bei Spotify Limited")))
