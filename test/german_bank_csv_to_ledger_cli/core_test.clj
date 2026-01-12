(ns german-bank-csv-to-ledger-cli.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [german-bank-csv-to-ledger-cli.core :refer [determine-recipient format-amount-to-euro vector-to-hashmap]]))

;["10.11.2021" "10.11.2021" "SEPA-Lastschrift von" "Drillisch Online GmbH" "C4413004 U440695502 B514217508 handyvertrag.de" "DE89506400151233322700" "COBADEFF506" "" "EF958053341760F03563C0DC35C" "DE40ZZZ10000206926" "" "" "" "" "" "-10,59" "" "EUR"])
; that is not the correct form anymore

(deftest test-determine-recipient-with-auftraggeber
  (testing "FIXME, determine-recipient")
  (is (= (determine-recipient
          {:Debit "-3.3", :Payment-Details "DM-Drogerie Markt",:Original-Amount -3.3, :Booking-date "12/17/2025", :Currency "EUR", :Value-date "12/17/2025",:Beneficiary-Originator "ABRECHNUNG KARTE",:Transaction-Type "Debit Card Payment"})
         "DM-Drogerie Markt")))


(deftest test-determine-recipient-with-paypal-2
  (testing "FIXME, determine-recipient-paypal with space after number")
  (is (= (determine-recipient
          {:Ultimate-creditor ""                                          :Mandate-Reference
           "5QKJ22TLWAL"                                               :Debit
           "-2.6"                                                        :Payment-Details
           "10741088411 . Rheinbahn AG, Ihr Einkauf bei Rheinbahn AG," :Customer-Reference
           "10741088411  PAYPAL"                                       :IBAN-Account-Number
           "DE8853033326303"                                      :Original-Amount
           "-2.6"                                                        :Booking-date
           "1/7/2026"                                                    :Currency
           "EUR"                                                         :Number-of-cheques
           :BIC                                                        :Value-date
           "1/7/2026"                                                    :Credit
           :Number-of-transactions                                     :Beneficiary-Originator
           "PayPal (Europe) S.a r.l. et Cie, S. C.A."                    :Transaction-Type
           "SEPA Direct Debit"                                           :Creditor-ID
           "LU96Z00000000000000058"                                  :Compensation-amount ""})
         ". Rheinbahn AG, Ihr Einkauf bei Rheinbahn AG,")))


(deftest test-determine-recipient-with-paypal
  (testing "FIXME, determine-recipient-paypal")
  (is (= (determine-recipient
          {:Debit "-5.5", :Payment-Details "1046952725146/. Rheinbahn AG, Ihr Einkauf bei Rheinbahn AG",
           :Booking-date "12/17/2025", :Currency "EUR",
           :Number-of-cheques , :BIC , :Value-date "12/17/2025",
           :Beneficiary-Originator "PayPal Europe S.a.r.l. et Cie S.C.A",
           :Transaction-Type "SEPA Direct Debit"})
         "/. Rheinbahn AG, Ihr Einkauf bei Rheinbahn AG")))

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
         {:Ultimate-creditor "",
          :Mandate-Reference "C3DQ6Q",
          :Debit "-1.14",
          :Payment-Details "Kartennr. 5354999999998542",
          :Customer-Reference "560545044755111911",
          :IBAN-Account-Number "DE19500700240004020480",
          :Original-Amount "-1.14",
          :Booking-date "11/21/2025",
          :Currency "EUR",
          :Number-of-cheques "",
          :BIC "",
          :Value-date "11/21/2025",
          :Credit "",
          :Number-of-transactions "",
          :Beneficiary-Originator "19-11-2025T17:59:42",
          :Transaction-Type "Debit Card Payment ABRECHNUNG KARTE REWE Duesseldorf,//Duesseldorf/DE",
          :Creditor-ID "DE1900200000106424",
          :Compensation-amount ""})))

