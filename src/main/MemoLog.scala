import net.townym.m2g._

object m2g extends M2G with M2GDebug {

  object params extends M2GParams {
    // mongodb query
    def runQuery = {
      import com.mongodb.casbah.Imports._
      val coll = MongoConnection("127.0.0.1", 60017)("cookpad")("extension.recipe_memo_ext")
      val js = MongoDBObject
      val q  = js("type"    -> "create",
                  "content" -> js("$ne" -> ""))
      coll.find( q, js() ).sort(js("ts" -> 1))
    }

    // result fields
    val keys = List("memo_id", "kind", "user_id", "recipe_id", "content", "ts", "ts_str", "version", "ua", "_id")

    //def auth(ctx: GDataAuth#Context) { authWithPassword(ctx, "ikuo-matsumura@cookpad.jp", null) }
    def auth(ctx: GDataAuth#Context) { authWithOAuth(ctx) }

    val bookName = "log_my_recipe_memo"
    val sheetName = "1. Create (B)"
  }

  // main
  def main(args:Array[String]) { cliMain(params, args) }
}
