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

(defn map-betreff-to-category [betreff]

  (get myconf/betreff-format (first (filter #(str/starts-with? betreff %) (keys myconf/betreff-format))) betreff))
(defn money-category [entry]
  (cond
    (= (get entry 3) "PayPal (Europe) S.a.r.l. et Cie., S.C.A.") (get myconf/paypal (get entry 4))
    (= (get entry 3) "") (map-betreff-to-category (get entry 4))
    :else
    (get myconf/money-category
         (get entry 3))))

(defn format-recipient [recipient]

  (get myconf/betreff-format (first (filter #(str/starts-with? recipient %) (keys myconf/recipient-format))) recipient))


(defn determine-recipient [entry]
  (cond
    (= (get entry 3) "") (format-recipient (get entry 4))
    (str/starts-with? (get entry 3) "PayPal (Europe) S.a.r.l. et Cie., S.C.A.") (second (str/split (get entry 4) #"Ihr Einkauf bei"))
    :else (get entry 3))
;
  )
(defn invert-string-amount [amount]

  (if (str/starts-with? amount "-") (subs amount 1) (str "-" amount)))

(defn determine-amount [entry]
  (if (not= (nth entry (- (count entry) 3)) "")
    (nth entry (- (count entry) 3))
    (nth entry (- (count entry) 2))))

(defn put-in-euro-sign [amount]
  (if (str/starts-with? amount "-") (str "-" "€" (subs amount 1)) (str "€" amount)))

(defn convert-to-ledger-format [entry]
  ;
  (apply str (str/join "/" (reverse (str/split (first entry) #"\.")))  " * " (determine-recipient entry) "\n"
         "\t" (money-category entry)  "  " (put-in-euro-sign (invert-string-amount (determine-amount entry))) "\n"
         "\tAssets:Bank:Checking  "    (put-in-euro-sign (determine-amount entry))  "\n"))
;
(defn -main
  "I don't do a whole lot ... yet."

  [& args]
  (if (= args nil) (println "Please provide a csv file!"))
; e.g."/home/dave/ClojureProjects/german-bank-csv-to-ledger-cli/src/german_bank_csv_to_ledger_cli/Kontoumsaetze_300_812603900_20211022_195241.csv")
  (->>
   (take-csv (first args))
   (partition-by #(str/starts-with? (first %) "Buchungstag"))
   (last)
   (map convert-to-ledger-format)
   (reduce str)
   (println)))


(-main "/home/michael/example.csv")

;run all unit tests
