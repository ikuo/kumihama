package net.townym.kumihama

// Parameters for M2G
abstract trait M2GParams {
  import com.mongodb.casbah._
  import com.mongodb.casbah.Imports._
  def runQuery: MongoCursorBase[DBObject]

  def auth(context: GDataAuth#Context)
  def keys: List[String]
  val bookName: String
  val sheetName: String
}

// mongodb to Google Spreadsheet
trait M2G extends GDataAuth {
  import com.google.gdata.data._
  import com.google.gdata.client._
  import com.google.gdata.client.spreadsheet._
  import com.google.gdata.data.spreadsheet._
  import collection.JavaConversions._

  // send query result to google spread sheet
  def saveToGData(params: M2GParams) {

    type Err = RuntimeException
    val srv = new SpreadsheetService("m2g")

    // authentication & authorization
    params.auth(srv)

    // get spread sheet (book)
    val book = {
      val query = new SpreadsheetQuery(FeedURLFactory.getDefault().getSpreadsheetsFeedUrl())
      query.setTitleQuery(params.bookName)
      srv.query(query, classOf[SpreadsheetFeed]).getEntries().toList match { case e :: Nil => e
        case _ :: _ :: Nil => throw new Err("Multiple book found for name: " ++ params.bookName)
        case Nil           => throw new Err("Book not found for name: " ++ params.bookName)
      }
    }

    // get work sheet
    val sheet  = book.getWorksheets().find( s => s.getTitle().getPlainText().equals(params.sheetName) ) match {
      case Some(s) => s
      case None    => throw new Err("Sheet not found: " ++ params.sheetName)
    }

    val rows = srv.getFeed(sheet.getListFeedUrl(), classOf[ListFeed])
    val list = params.runQuery.skip(sheet.getRowCount() - 1)
    print("Adding " ++ list.size.toString ++ " rows ")
    for (obj <- list) {
      print('.')
      val row = new ListEntry()
      params.keys.foreach(key => { row.getCustomElements().setValueLocal( key.filter(_ != '_'), obj.get(key).toString ) } )
      rows.insert(row)
    }
    println
  }

  // default main for CLI
  def cliMain(params:M2GParams, args :Array[String]) {
    def print_usage { println("Usage: sbt run") }
    args.size match {
      case 0 => saveToGData(params)
      case _ => print_usage
    }
  }
}

// Debugging facility for M2G
trait M2GDebug {
  // CSV related stuff
  import java.io.{File, PrintWriter}

  // save to local csv file
  def saveToCSV(params: M2GParams, file: File = new File("out.csv")) {
    withPrintWriter(file, writer =>
      for (obj <- params.runQuery) {
        writer.println( params.keys.map(obj.get(_)).mkString(", ") )
      }
    )
  }

  def withPrintWriter(file:File, f:PrintWriter => Unit) {
    val writer = new PrintWriter(file)
    try { f(writer) } finally { writer.close() }
  }
}
