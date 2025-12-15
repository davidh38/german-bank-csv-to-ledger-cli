(ns german-bank-csv-to-ledger-cli.io
  (:require [clojure-csv.core :as csv]
            [clojure.java.io :as javaio]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [hiccup.page :refer [html5]]
            [german-bank-csv-to-ledger-cli.core :as core]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.refresh :refer [wrap-refresh]]
            [ring.middleware.reload :refer [wrap-reload]]
            [clojure.edn :as edn]) (:gen-class))




(defn slurp-edn [path]
  (edn/read-string (slurp path)))

(defn take-csv
  "Takes file name and reads data."
  [fname]
  (with-open [file (javaio/reader fname)]
    (csv/parse-csv  (slurp file) :delimiter \;)))

(defn save-edn-file [data]
  (spit "/home/dave/german-bank-csv-to-ledger-cli/src/german_bank_csv_to_ledger_cli/conf.edn" (pr-str data)))

(defn save-config [payee expense]
  (save-edn-file
   (assoc  (slurp-edn "/home/dave/german-bank-csv-to-ledger-cli/src/german_bank_csv_to_ledger_cli/conf.edn")
           payee expense)))


(defn myhtml5 [data]
  (map (fn [x] [:tr {:data-date (:date x) :data-payee (:payee x) :data-currency (:payee x) :data-creditaccount (:credit_account x)  :data-debit (:debit_account x)
                     :data-amount (:amount x)}
                [:td
                 [:div (:date x)]]
                [:td [:div (:payee x)]]
                [:td [:div (:debit_amount x)]]
                [:td [:div (:currency x)]]
                [:td [:div (:credit_account x)]]
                [:td [:a {:href "test"} (:debit_account x)]]]) data))


(defn hiccup [data] (html5
                     [:head]
                     [:link {:rel "stylesheet" :href "styles.css"}]
                     [:script {:src "javascript.js" :defer true}]
                     [:body
                      [:div {:id "popup" :class "popup"}
                       [:div {:class "popup-content"}
                        [:span {:id "close" :class "close"} "&times"]
                        [:h2 "Details"]
                        [:p "Date: " [:span {:id "pDate"}]]
                        [:p "Payee: " [:input {:type "text" :id "pPayee"}]]
                        [:p "Debit: " [:input {:type "text" :id "pDebit"}]]
                        [:p "Credit: " [:span {:id "pCredit"}]]
                        [:button {:id "savePopup"} "Save"]]]
                      [:button {:id "copybutton" :class "button"} "Copy to clipboard"]
                      [:div {:id "mytable"}
                       [:table
                        (myhtml5 data)]]]))



(defn getdata [file-path]
  (core/convert-csv-to-hashmap
   (take-csv file-path)
   (slurp-edn "/home/dave/german-bank-csv-to-ledger-cli/src/german_bank_csv_to_ledger_cli/conf.edn")))

(defn make-handler [file-path]
  (fn [req]
    (case [(:request-method req) (:uri req)]
      [:get "/"] {:status 200
                  :headers {"Content-Type" "text/html"}
                  :body (hiccup (getdata file-path))}
      [:get "/copy"] {:status 200
                      :headers {"Content-Type" "text/plain"}
                      :body (core/convert-csv-to-string (take-csv file-path)
                                                        (slurp-edn "/home/dave/german-bank-csv-to-ledger-cli/src/german_bank_csv_to_ledger_cli/conf.edn"))}
      [:post "/mytest"]
      (let [params (:params req)
            a      (get params "a")
            b      (get params "b")]
        (if (and a b)
          (do
            (println a)
            (println b)
            (save-config a b)   ;; ✅ called BEFORE return
            {:status 302
             :headers {"Location" "/"}
             :body ""})
          {:status  400
           :headers {"Content-Type" "text/plain"}
           :body    "Missing params"}))
      {:status 404
       :headers {"Content-Type" "text/plain"}
       :body "Not Found"})))

; server
(defn -main [args]
  (->  (make-handler args)
       (wrap-resource "public")
       (wrap-params)
       (wrap-reload)
       (wrap-refresh)
       (run-jetty {:port 3002 :join? false}))
  (.exec (Runtime/getRuntime) "chromium-browser http://localhost:3002"))


;
;fetch("/mytest", {
;  method: "POST",
;  headers: {
;    "Content-Type": "application/x-www-form-urlencoded"
;  },
;  body: new URLSearchParams({
;    a: "123",
;    b: "456"
;  })
;})
;.then(r => r.text())
;.then(console.log);
;
;main

(comment
  (+ 1 1)
  (+ 2 1))

(comment
  (defn -main [args]
    (->>
     (getdata args)
     (map core/build-string-entry-for-ledger)
     (reduce str)
     (println))))
;(-main "/home/dave/Downloads/Transactions_300_8126039_00_20251121_171738.csv")

; 
;I need an export side for the strings
; append to
;I need an table side
;  "denn.s Biomarkt"                                          "Expenses:Food:Supermarket"