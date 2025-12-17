FROM clojure:latest

# Set working directory
WORKDIR /app

# Copy project dependency file
COPY project.clj .

# Download dependencies (cached layer unless project.clj changes)
RUN lein deps

COPY resources/ resources/

# Copy source code (excluding the broken symlink)
COPY src/ src/

# Remove both .conf.clj and the broken symlink conf.edn  
RUN rm -f src/german_bank_csv_to_ledger_cli/conf.edn

# Copy the actual config file from the build context
COPY src/german_bank_csv_to_ledger_cli/.conf.edn  src/german_bank_csv_to_ledger_cli/conf.edn

# Set entrypoint
ENTRYPOINT ["lein", "run"]

# sudo docker run -v /home/dave/Downloads/Transactions_300_20251026_093214.csv:/data/input.csv:z germanbank /data/input.csv
