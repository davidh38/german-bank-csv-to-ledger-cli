(
 ns german-bank-csv-to-ledger-cli.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [german-bank-csv-to-ledger-cli.core :refer [determine-recipient format-amount-to-euro vector-to-hashmap]]))

;["10.11.2021" "10.11.2021" "SEPA-Lastschrift von" "Drillisch Online GmbH" "C4413004 U440695502 B514217508 handyvertrag.de" "DE89506400151233322700" "COBADEFF506" "" "EF958053341760F03563C0DC35C" "DE40ZZZ10000206926" "" "" "" "" "" "-10,59" "" "EUR"])
; that is not the correct form anymore
(def testdata {:date           "10.11.2021"
               :booked-sign    "*"
               :payee          "Drillisch Online GmbH"
               :credit_account "Assets:Bank:Checking"
               :credit_amount  ""
               :debit_account  "Expenses:Food:HotAndColdDrinks"
               :debit_amount   "-17.1"
               :currency       "EUR"})


(deftest test-determine-recipient-with-auftraggeber
  (testing "FIXME, determine-recipient")
  (is (= (determine-recipient testdata)
         "Drillisch Online GmbH")))

(deftest test-determine-recipient-with-betreff
  (testing "FIXME, determine-recipient-betreff")
  (is (= (determine-recipient ["30.12.2021" "30.12.2021" "Kartenzahlung" "" "NETTO MARKEN-DISCOU//DUESSELDORF/DE 29-12-2021T21:34:38 Folgenr. 07 Verfalld. 1523"  "" "" "" "-20,20"  "EUR"])
         "NETTO MARKEN-DISCOU//DUESSELDORF/DE 29-12-2021T21:34:38 Folgenr. 07 Verfalld. 1523")))

(deftest test-determine-recipient-with-paypal
  (testing "FIXME, determine-recipient-paypal")
  (is (= (determine-recipient ["29.12.2021" "29.12.2021" "SEPA Direct Debit" "PayPal Europe S.a.r.l. et Cie S.C.A" ". Spotify Limited, Ihr Einkauf bei Spotify Limited" "DE88500700102175526303" "DEUTDEFHXXX" "1017624592854  PAYPAL" "5QKJ223MTLWAL" "LU96ZZZ0000000000000000058" "" "" "" "" "" "-9,99" "" "EUR"])
         ". Spotify Limited, Ihr Einkauf bei Spotify Limited")))

(deftest test-format-amount-to-euro
  (testing "FIXME, format-amount-to-euro")
  (is (= (format-amount-to-euro "9.60")
         "9,60 EUR")))

(deftest test-format-amount-to-euro-bigger-thousand
  (testing "FIXME, format-amount-to-euro")
  (is (= (format-amount-to-euro "1119.60")
         "1119,60 EUR")))

(deftest test-vector-to-hash
  (testing "FIXME, vector-to-hash")
  (is (= (vector-to-hashmap
          ["11/21/2025"
           "11/21/2025"
           "Debit Card Payment ABRECHNUNG KARTE REWE Duesseldorf,//Duesseldorf/DE"
           "19-11-2025T17:59:42"
           "Kartennr. 5354999999998542"
           "DE19500700240004020480"
           ""
           "560545044755111911"
           "C3DQ6Q"
           "DE1900200000106424"
           ""
           "-1.14"
           ""
           ""
           ""
           "-1.14"
           ""
           "EUR"])
         "test")))


