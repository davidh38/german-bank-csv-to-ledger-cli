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
  (spit "./src/german_bank_csv_to_ledger_cli/conf.edn" (pr-str data)))

(defn save-config [payee expense]
  (save-edn-file
   (assoc  (slurp-edn "./src/german_bank_csv_to_ledger_cli/conf.edn")
           payee expense)))


(defn transaction-table [data]
  (map (fn [x]

         [:tr {:data-date (:date x) :data-payee (:payee x) :data-currency (:payee x) :data-creditaccount (:credit_account x)  :data-debit (:debit_account x)
               :data-amount (:amount x)}
          [:td
           [:div (:date x)]]
          [:td [:div (:payee x)]]
          [:td [:div (core/determine-debit-or-credit-amount x)]]
          [:td [:div (:currency x)]]
          [:td [:div (:credit_account x)]]
          [:td [:div (:debit_account x)]]]) data))


(defn header [data] (html5
                     [:head]
                     [:link {:rel "stylesheet" :href "styles.css"}]
                     [:script {:src "javascript.js" :defer true}]
                     [:body
                      [:div {:id "popup" :class "popup"}
                       [:div {:class "popup-content"}
                        [:span {:id "close" :class "close"} "&times"]
                        [:h2 "Details"]
                        [:p "Date: " [:span {:id "pDate"}]]
                        [:p "Payee: " [:input {:type "text" :id "pPayee" :class "inputtext"}]]
                        [:p "Account: " [:input {:type "text" :id "pDebit" :class "inputtext"}]]
                        [:button {:id "savePopup" :class "button"} "Save"]]]
                      [:div {:id "layout"}
                       [:button {:id "copybutton" :class "button"} "Copy to clipboard"]
                       [:div {:id "mytable"}
                        [:table {:class "hc-table"}
                         [:thead
                          [:tr
                           [:th "Date"]
                           [:th "Payee"]
                           [:th "Amount"]
                           [:th "Currency"]
                           [:th "Account"]
                           [:th "Account"]]]
                         [:tbody
                          (transaction-table data)]]]]]))



(defn getdata [file-path]
  (core/convert-csv-to-hashmap
   (take-csv file-path)
   (slurp-edn "./src/german_bank_csv_to_ledger_cli/conf.edn")))

(defn make-handler [file-path]
  (fn [req]
    (case [(:request-method req) (:uri req)]
      [:get "/"] {:status 200
                  :headers {"Content-Type" "text/html"}
                  :body (header (getdata file-path))}
      [:get "/copy"] {:status 200
                      :headers {"Content-Type" "text/plain"}
                      :body (core/convert-csv-to-string (take-csv file-path)
                                                        (slurp-edn "./src/german_bank_csv_to_ledger_cli/conf.edn"))}
      [:post "/mytest"]
      (let [params (:params req)
            a      (get params "a")
            b      (get params "b")]
        (if (and a b)
          (do
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
 ; (.exec (Runtime/getRuntime) "chromium-browser http://localhost:3002")
  )

(comment
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
)
