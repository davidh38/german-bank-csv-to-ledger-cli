(ns german-bank-csv-to-ledger-cli.core
  (:require [clojure.string :as str]))

(defn invert-string-amount [amount]
  (if (str/starts-with? amount "-") (subs amount 1) (str "-" amount)))

(defn format-amount-to-euro [amount]
  (->
   (str/replace amount "," "") ; for > 1000
   (str/replace "." ",")
   (str " EUR")))

(defn determine-recipient
  "return beneficiary except for abrechnung, karte"
  [entry]
  (cond
    (= (:Beneficiary-Originator entry) "PayPal Europe S.a.r.l. et Cie S.C.A") (str/replace (:Payment-Details entry) #"^\d+" "") ;delete preceding unique numbers, if paypal
    (= (:Beneficiary-Originator entry) "PayPal (Europe) S.a r.l. et Cie, S. C.A.") (str/replace (:Payment-Details entry) #"^\d+" "") ;delete preceding unique numbers, if paypal
    (= (:Beneficiary-Originator entry) "") (:Payment-Details entry)
    (= (str/lower-case  (:Beneficiary-Originator entry)) "abrechnung karte") (:Payment-Details entry)
    :else
    (:Beneficiary-Originator entry)))

(comment
  (determine-recipient
   {:Debit "-5.5", :Payment-Details "1046952725146/. Rheinbahn AG, Ihr Einkauf bei Rheinbahn AG",
    :Booking-date "12/17/2025", :Currency "EUR",
    :Number-of-cheques , :BIC , :Value-date "12/17/2025",
    :Beneficiary-Originator "PayPal Europe S.a.r.l. et Cie S.C.A",
    :Transaction-Type "SEPA Direct Debit"}))

(defn determine-money-category
  "if the string starts with a key the value should be evaluated"
  [conf_map ledger-entry]
  (assoc
   ledger-entry
   :debit_account
   (get conf_map
        (first
         (filter
          #(str/starts-with? (:payee ledger-entry) %) (keys conf_map))) "Uncategorized")))

(defn determine-debit-or-credit-amount [ledger-entry]
  (if (not= (:debit_amount ledger-entry) "")
    (:debit_amount ledger-entry)
    (:credit_amount ledger-entry)))


(defn vector-to-hashmap [transaction]
  (let [transactionkeys [:Booking-date
                         :Value-date
                         :Transaction-Type
                         :Beneficiary-Originator
                         :Payment-Details
                         :IBAN-Account-Number
                         :BIC
                         :Customer-Reference
                         :Mandate-Reference
                         :Creditor-ID
                         :Compensation-amount
                         :Original-Amount
                         :Ultimate-creditor
                         :Number-of-transactions
                         :Number-of-cheques
                         :Debit
                         :Credit
                         :Currency]]
    (zipmap transactionkeys transaction)))

(defn csv-entry-to-ledger [csv-transaction]
; copy normal
  (let [ledger-transaction
        {:date ""
         :booked-sign "*"
         :payee ""
         :credit_account "Uncategorized"
         :credit_amount ""
         :debit_account ""
         :debit_amount ""
         :currency ""}]

    (-> ledger-transaction
        (assoc :date (:Booking-date csv-transaction))
        (assoc :debit_account (:money-category csv-transaction))
        (assoc :debit_amount (:Debit csv-transaction))
        (assoc :currency (:Currency csv-transaction))
        (assoc :credit_account "Assets:Bank:Checking")
        (assoc :credit_amount (:Credit csv-transaction))
        (assoc :payee (determine-recipient csv-transaction)))))

(defn build-string-entry-for-ledger [entry]
  (let [date-parts (str/split (:date entry) #"/")
        year (nth date-parts 2)
        month (nth date-parts 0)
        day (nth date-parts 1)
        formatted-date (str year "/" month "/" day)]
    (str formatted-date " * " (:payee entry) "\n"
         "\t" (:debit_account entry) "  " (format-amount-to-euro (invert-string-amount (determine-debit-or-credit-amount entry))) "\n"
         "\tAssets:Bank:Checking  "    (format-amount-to-euro (determine-debit-or-credit-amount entry))  "\n")))

(defn convert-csv-to-hashmap [csvfile myconf]
  (->>
   csvfile ; returns sequence of vectors
   ;partition-by uses first, because every transaction is a list
   (partition-by #(str/starts-with? (first %) "Booking date"))
   (last)
   ;delete last line, because it is no valid transaction
   (butlast)
   ;puts the list of vectors and puts it into the correct hashmap
   (map vector-to-hashmap)
   (map csv-entry-to-ledger)
   (map #(determine-money-category myconf %))

   ))

;lein run /home/dave/Downloads/Transactions_300_20251130_155558.csv" > ./output
;(convert-csv-to-hashmap "/home/dave/Downloads/Transactions_300__20251130_154052.csv")

 