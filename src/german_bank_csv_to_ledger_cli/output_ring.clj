(ns german-bank-csv-to-ledger-cli.output-ring
  (:import (java.io File))
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [clojure.string :as str]
            [hiccup.page :refer [html5]]
            [clojure.java.io :as io]))

;; Sample transactions for testing
(def sample-transactions
  "2011/03/15 * Trader Joe's
    Expenses:Groceries  100.00 EUR
    Assets:Checking
2011/03/15 * Whole Food Market
    Expenses:Groceries  75.00 EUR
    Assets:Checking")

;; Your ledger entry as a Clojure string
(def tx (try (slurp "./output")
             (catch Exception _ sample-transactions)))

;; Parse a single transaction from ledger format
(defn parse-transaction [tx-text]
  (let [lines (str/split-lines tx-text)
        first-line (first lines)
        [date-and-status description] (str/split first-line #"\s+\*\s+" 2)
        date (str/trim date-and-status)
        postings (for [line (rest lines)
                       :when (not (str/blank? line))]
                   (let [trimmed (str/trim line)
                         parts (str/split trimmed #"\s{2,}")
                         account (first parts)
                         amount (second parts)]
                     {:account account
                      :amount (or amount "")}))]
    {:date date
     :description description
     :postings postings}))

;; Parse all transactions from the ledger text
(defn parse-transactions [tx-str]
  (->> (str/split tx-str #"(?=\d{4}/\d{1,2}/\d{1,2})")
       (filter #(not (str/blank? %)))
       (map parse-transaction)))

(defn transaction-row
  [index {:keys [date description postings]}]
  (let [bg-class (if (odd? index) "bg-gray-100" "bg-white")]
    [:li {:class (str "py-4 px-4 mb-2 rounded-lg " bg-class)}
     [:div {:class "flex flex-col gap-2"}
      [:div {:class "flex justify-between items-start"}
       [:div
        [:p {:class "text-sm font-semibold text-gray-900"} date]
        [:p {:class "text-sm text-gray-700 mt-1"} description]]
       [:div {:class "text-right"}
        (when-let [first-posting (first postings)]
          [:p {:class "text-sm font-medium text-gray-900"} (:amount first-posting)])]]
      [:div {:class "ml-4 mt-2 space-y-1"}
       (for [posting postings]
         ^{:key (:account posting)}
         [:div {:class "flex justify-between text-xs"}
          [:span {:class "text-gray-600"} (:account posting)]
          [:span {:class "text-gray-800 font-mono"} (:amount posting)]])]]]))

(defn transaction-list [transactions]
  [:ul {:role "list" :class "space-y-0"}
   (map-indexed
    (fn [idx tx]
      ^{:key (str (:date tx) (:description tx))} (transaction-row idx tx))
    transactions)])

(defn transaction-page [transactions]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:title "Ledger Transactions"]
    [:script {:src "https://cdn.tailwindcss.com"}]]
   [:body {:class "bg-gray-50 p-8"}
    [:div {:class "max-w-4xl mx-auto"}
     [:h1 {:class "text-2xl font-bold mb-6 text-gray-900"} "Ledger Transactions"]
     [:div {:class "bg-white rounded-lg shadow p-6"}
      (transaction-list transactions)]]]))

;; simple HTML-escape so the ledger text prints correctly
(defn html-escape [s]
  (str/escape s {\& "&amp;"
                 \< "&lt;"
                 \> "&gt;"
                 \" "&quot;"}))

(defn colorize-entries [tx-str]
  (str/replace tx-str
               #"(?m)^\d{4}/\d{1,2}/\d{1,2}\s+\*[^\n]*(?:\n(?!\d{4}/\d{1,2}/\d{1,2}\s+\*)[^\n]*)*"
               (fn [match]
                 (if (re-find #"(?m)^\s+test\s+" match)
                   (str "<span style=\"color: #ef4444;\">" (html-escape match) "</span>")
                   (html-escape match)))))

(defn ledger->html [tx-str]
  (format "<!doctype html>
<html lang=\"en\">
<head>
  <meta charset=\"utf-8\">
  <title>Ledger Entry</title>
  <!-- optional Tailwind via CDN -->
  <script src=\"https://cdn.tailwindcss.com\"></script>
</head>
<body class=\"bg-slate-900 text-slate-100 flex items-center justify-center min-h-screen\">
  <div class=\"max-w-4xl w-full p-6\">
    <h1 class=\"text-xl font-semibold mb-4\">Ledger Entry</h1>
    <pre class=\"whitespace-pre font-mono bg-slate-800 rounded-lg p-4 text-sm overflow-x-auto\">
%s
    </pre>
  </div>
</body>
</html>
" (colorize-entries tx-str)))

(defn handler [_]
  (let [transactions (parse-transactions tx)]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (transaction-page transactions)}))

(defn -main [& _]
  (run-jetty handler {:port 3000 :join? false})
  (.exec (Runtime/getRuntime) "chromium-browser http://localhost:3000"))


;lein run -m german-bank-csv-to-ledger-cli.output-ring.clj