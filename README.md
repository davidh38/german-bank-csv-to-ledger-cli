# german-bank-csv-to-ledger-cli

Converts german bank.csv to ledger cli format and chooses the the category which you configured in the config.

## Installation

    git clone

## Usage

FIXME: explanation
    
    change .conf.clj to conf.clj
    use the configuration to adapt the program to your purposes
    lein ueberjar
    $ java -jar german-bank-csv-to-ledger-cli-0.1.0-standalone.jar [file]

## Examples

this is the ledger-cli format:

2021/08/13 *  DROPBOXINTE
	Expenses:Leisure:Digital:Storage  €11,99
	Assets:Bank:Checking  -€11,99
2021/08/12 * Expenses:Food:Supermarket
	Expenses:Food:Supermarket  €1,36
	Assets:Bank:Checking  -€1,36

# Errors

add errors with no arguments

# Requirements

-todo write down the exact requirements


## Todos

- put io outside
- rewrite the config into a more clear format
