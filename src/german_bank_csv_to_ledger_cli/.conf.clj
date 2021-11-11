
(ns german-bank-csv-to-ledger-cli.conf)

(def paypal {"PP.7079.PP . HUEL, Ihr Einkauf bei HUEL" "Expenses:Food:Powder"
             "PP.7079.PP . MANA DACH, Ihr Einkauf bei MANA DACH" "Expenses:Food:Powder"
})

(def money-category     {"Telefonica Germany GmbH + Co. OHG" "Expenses:Leisure:Digital:Internet"
                         "1u1 MAILUMEDIA GMBH-WEB.DE" "Expenses:Leisure:Digital:Email"})

(def betreff-format {"ALDI" "Expenses:Food:Supermarket"
                     "TK Maxx" "Expenses:Leisure:Clothes:Outfit"
                     })

(def recipient-format
{"ALDI" "ALDI"
 "TK Maxx" "TK Maxx"
 })
