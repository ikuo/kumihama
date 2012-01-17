# Kumihama
## A bridge from MongoDB to Google Spreadsheet by scala
## Features
* OAuth support as well as email/password auth
* Incremental (delta) update of rows over multiple executions

## Build Dependency
* Download gdata-*.jar of [gdata-java-client](http://code.google.com/p/gdata-java-client/downloads/list) into lib/ directory

## Configuring
1. Rename `src/main/scala/MyApp.scala.sample` to `MyApp.scala`
2. Edit `MyApp.scala` to meet your 1) MongoDB query and 2) book/worksheet name of Google Spreadsheet
3. Prepare a worksheet
  - Create spreadsheet/worksheet of Google Spreadsheet with the name specified in your `MyApp.scala`
  - Delete all empty rows from the worksheet

## Running
Run sbt from the shell

    $ sbt run

[At first time] a google oauth URL will be opened or displayed. After finishing the auth process, a pair of OAuth access token and secret will be stored into `./oauth_access.token`.

[At every time]
When it successfully finished, a message like the following should be displayed.

    [info] Running m2g
    Adding 2 rows
    [success] Total time: 4 s, completed 2011/12/29 17:05:33

## Running in a batch
Run the following command and you will get an all-in-one jar at `./target/KumihamaApp-assembly-1.0.jar`

    $ sbt assembly

Put `./oauth_access.token` and the all-in-one jar into a directory (as $KUMIHAMA_APP1) on a server that runs batches.
Execute the following command in a batch (e.g. cron):

    cd $KUMIHAMA_APP1 && java -jar KumihamaApp-assembly-1.0.jar

## Known issues
### sleep interrupted
It throws InterruptedException at the end of run:

    ?x??: sleep interrupted
    java.lang.InterruptedException: sleep interrupted
            at java.lang.Thread.sleep(Native Method)
            at com.mongodb.DBApiLayer$DBCleanerThread.run(DBApiLayer.java:493)
            at java.lang.Thread.run(Thread.java:680)
    2012/01/17 9:14:09 com.mongodb.DBApiLayer$DBCleanerThread run
    ?x??: sleep interrupted
    java.lang.InterruptedException: sleep interrupted
            at java.lang.Thread.sleep(Native Method)
            at com.mongodb.DBApiLayer$DBCleanerThread.run(DBApiLayer.java:493)
            at java.lang.Thread.run(Thread.java:680)

See also: http://groups.google.com/group/liftweb/msg/c99b8d67a9450c55
