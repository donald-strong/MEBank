# MEBank

This application takes a file of transactions and calculates a relative account balance for a selected account.
The transactions are constrained by a from time and a to time. Only transactions within this range are considered
in the relative balance, except that transactions that are reversed are excluded even if the reversal is outside 
the time period.

## Build

    $ mvn clean install

## Display usage

    $ java -jar target/MEBank-0.jar
    Error: Must be 4 command line arguments
    Usage: java -jar <jarfile> <filename> <account_id> <from_date_time> <to_date_time>
        <jarfile> the path to the jar file (Not really a parameter)
        <filename> the path to the transaction file
        <account_id> the account ID
        <from_date_time> the time from which to include transations
        <to_date_time> the time to which to include transactions
    E.g. $ java -jar MEBank.jar Sample.csv ACC334455 '20/10/2018 12:00:00' '20/10/2018 19:00:00'
    
## Process files

    $ java -jar target/MEBank-0.jar src/test/resources/SampleOne.csv ACC334455 '20/10/2018 12:00:00' '20/10/2018 19:00:00'
    Relative balance for the period is: -$25.00
    Number of transactions included is: 1
    $ java -jar target/MEBank-0.jar src/test/resources/SampleTransactions.csv ACC334455 '20/10/2018 12:00:00' '20/10/2018 19:00:00'
    Relative balance for the period is: -$25.00
    Number of transactions included is: 1
    $ java -jar target/MEBank-0.jar src/test/resources/SampleTransactions.csv ACC778899 '20/10/2018 12:00:00' '21/10/2018 12:00:00'
    Relative balance for the period is: $37.25
    Number of transactions included is: 3

## Comments and assumptions

* The transaction file is assumed to be comma separated without leading whitespace on the fields. 
  This is a limitation of the OpenCSV library used.
* Lines in the transaction file without a relatedTransaction value must have a trailing comma.
* The number of transactions in the file is assumed to be not too large as they are all read into memory.
* Reversals are assumed to be rare as the payments list is traversed sequentially.

