(ns german-bank-csv-to-ledger-cli.output-html
  (:import (java.io File))
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

;; Your ledger entry as a Clojure string
(def tx (slurp "./output"))

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


(defn open-html-in-browser []
  (let [html (ledger->html tx)]
    (spit "ledger-entry.html" html)
    (println "Opening" "ledger-entry.html" "in browser...")
    (.exec (Runtime/getRuntime) (str "chromium-browser " "ledger-entry.html"))))

(defn -main [& args]
  (open-html-in-browser))

;lein run -m german-bank-csv-to-ledger-cli.output-ring