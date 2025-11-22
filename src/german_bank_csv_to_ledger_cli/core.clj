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

(defn determine-amount [entry_coll]
  (if (not= (nth entry_coll (- (count entry_coll) 3)) "")
    (nth entry_coll (- (count entry_coll) 3))
    (nth entry_coll (- (count entry_coll) 2))))

(defn put-in-euro-sign [amount]
  (str (str/replace amount "." ",") " EUR"))

(defn determine-money-category
  "if the string starts with a key the value should be evaluated"
  [conf_map recipient]
  (get conf_map
       (first
        (filter
         #(str/starts-with? recipient %) (keys conf_map))) "test"))

(defn build-entry-for-ledger [entry recipient money-category]
  (let [date-parts (str/split (first entry) #"/")
        year (nth date-parts 2)
        month (nth date-parts 0)
        day (nth date-parts 1)
        formatted-date (str year "/" month "/" day)]
    (str formatted-date " * " recipient "\n"
         "\t" money-category  "  " (put-in-euro-sign (invert-string-amount (determine-amount entry))) "\n"
         "\tAssets:Bank:Checking  "    (put-in-euro-sign (determine-amount entry))  "\n")))

(defn determine-recipient
  "in case of paypal as auftraggeber or empty auftraggeber return betreff entry as recipient
     in all other cases return auftraggeber as recipient"
  [entry]
  (cond
    (= (get entry 3) "PayPal Europe S.a.r.l. et Cie S.C.A") (str/replace (get entry 4) #"^\d+" "") ;delete preceding unique numbers, if paypal
    (= (get entry 3) "ABRECHNUNG KARTE") (get entry 4)
    (= (get entry 3) "") (get entry 4)
    :else
    (get entry 3)))

(defn convert-to-ledger-format
  "determine the receiver and from that the category"
  [conf entry]

  (let [recipient (determine-recipient entry)]
    (->>
     (determine-money-category conf recipient)
     (build-entry-for-ledger entry recipient))))

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
   ;takes a seq of vectors and maps it to a sequence of str
   (map (partial convert-to-ledger-format myconf/recipient-to-moneycategory))
   (reduce str)
   (println)))

;lein run "/home/dave/Downloads/Transactions_300_8126039_00_20251121_171738.csv" > ./output

