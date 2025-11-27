(ns german-bank-csv-to-ledger-cli.core
  (:require [clojure-csv.core :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [german-bank-csv-to-ledger-cli.conf :as myconf]) (:gen-class))

(defn take-csv
  "Takes file name and reads data."
  [fname]
  (with-open [file (io/reader fname)]
    (csv/parse-csv  (slurp file) :delimiter \;)))

(defn invert-string-amount [amount]
  (if (str/starts-with? amount "-") (subs amount 1) (str "-" amount)))

(defn determine-amount-of-tx [entry_coll]
  (if (not= (nth entry_coll (- (count entry_coll) 3)) "")
    (nth entry_coll (- (count entry_coll) 3))
    (nth entry_coll (- (count entry_coll) 2))))

(defn format-amount-to-euro [amount]
  (->
   (str/replace amount "," "") ; for > 1000
   (str/replace "." ",")
   (str " EUR")))

(defn determine-recipient
  "in case of paypal as auftraggeber or empty auftraggeber return betreff entry as recipient
     in all other cases return auftraggeber as recipient"
  [entry]
  (cond
    (= (:Transaction-Type entry) "PayPal Europe S.a.r.l. et Cie S.C.A") (str/replace (:Payment-Details entry) #"^\d+" "") ;delete preceding unique numbers, if paypal
    (= (:Transaction-Type entry) "ABRECHNUNG KARTE") (:Payment-Details entry)
    (= (:Transaction-Type entry) "") (:Payment-Details entry)
    :else
    (:Beneficiary-Originator entry)))

(defn determine-money-category
  "if the string starts with a key the value should be evaluated"
  [conf_map ledger-entry]
  (assoc
   ledger-entry
   :account
   (get conf_map
        (first
         (filter
          #(str/starts-with? (:payee ledger-entry) %) (keys conf_map))) "Uncategorized")))

(defn build-entry-for-ledger [entry]
  (let [date-parts (str/split (:Booking-date entry) #"/")
        year (nth date-parts 2)
        month (nth date-parts 0)
        day (nth date-parts 1)
        formatted-date (str year "/" month "/" day)]
    (str formatted-date " * " "recipient" "\n"
         "\t" (:money-category entry) "  " (format-amount-to-euro (invert-string-amount (determine-amount-of-tx entry))) "\n"
         "\tAssets:Bank:Checking  "    (format-amount-to-euro (determine-amount-of-tx entry))  "\n")))


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
        {:Booking-date ""
         :booked-sign "*"
         :payee ""
         :account "Uncategorized"
         :amount_account ""
         :asset ""
         :amount_asset ""}]

    (-> ledger-transaction
        (assoc :Booking-date (:Booking-date csv-transaction))
        (assoc :payee (determine-recipient csv-transaction)))))

;   (determine-recipient entry)
;   (determine-money-category conf (determine-recipient entry))

(defn -main
  [args]
  (cond (= args nil)
        (println "Please provide a csv file!"))
  (println "------####### Starting program #####------")
  (->>
   (take-csv args) ; returns sequence of vectors
   ;partition-by uses first, because every transaction is a list
   (partition-by #(str/starts-with? (first %) "Booking date"))
   (last)
   ;((fn [lst] (take (- (count lst) 1) lst))) 
   ;delete last line, because it is no valid transaction
   (butlast)
   ;puts the list of vectors and puts it into the correct hashmap
   (map vector-to-hashmap)
  ; build 
   (map csv-entry-to-ledger)

   (map #(determine-money-category myconf/recipient-to-moneycategory %))
;   (map build-entry-for-ledger)
 ;  (map hashmap list1 list2)

   ;put vectors into map


   ;(map (partial convert-to-ledger-format myconf/recipient-to-moneycategory))
;  (reduce str)
;   (take 2)
   (println)))
;lein run "/home/dave/Downloads/Transactions_300_8126039_00_20251121_171738.csv" > ./output

(-main "/home/dave/Downloads/Transactions_300_8126039_00_20251121_171738.csv")
