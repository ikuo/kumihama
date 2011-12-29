# Kumihama
## A bridge from MongoDB to Google Spreadsheet by scala
## Features
* OAuth support as well as email/password auth
* Incremental (delta) update of rows between executions

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

At first time, google oauth URL will be opened. When you continue, OAuth access token will be stored into `./oauth_access.token`.

If it successfully finished, a message like the following should be displayed.

    [info] Running m2g
    Adding 2 rows
    [success] Total time: 4 s, completed 2011/12/29 17:05:33
