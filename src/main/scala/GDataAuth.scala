package net.townym.kumihama

trait GDataAuth {
  import com.google.gdata.client._
  type Context = GoogleService

  def authWithPassword(ctx:Context, email:String, passwd:String) {
    val pw = passwd match {
      case null => Console.withIn(System.in) { Console.readLine("Google account password: ") }
      case _    => passwd
    }
    println()
    ctx.setUserCredentials(email, pw)
  }

  def authWithOAuth(ctx:Context,
                    consumerKey:String    = "sandbox1.townym.net",
                    consumerSecret:String = "Fouqnir007jSDkVkOKCHEDGj",
                    scopeURL:String = null,
                    port:Int = 9101) {
    import java.io.{File, PrintWriter}
    import scala.io.Source
    import com.google.gdata.client.authn.oauth._
    type Err = RuntimeException

    val params = new GoogleOAuthParameters()
    params.setOAuthConsumerKey(consumerKey)
    params.setOAuthConsumerSecret(consumerSecret)

    val file = new File("oauth_access.token")

    // get token file
    if (!file.exists()) {
      val helper = new GoogleOAuthHelper(new OAuthHmacSha1Signer())

      getRequestToken(helper, params)
      getAuthorizeToken(helper, params)
      saveAccessToken(file, params)
    } else {
      // read token file
      val it = Source.fromFile(file).getLines;
      val token  = if (it.hasNext) it.next else throw new Err("Could not read token from file: " ++ file.toString)
      val secret = if (it.hasNext) it.next else throw new Err("Could not read token from file: " ++ file.toString)
      if (it.hasNext) throw new Err("Extra string found in: " ++ file.toString)
      params.setOAuthToken(token)
      params.setOAuthTokenSecret(secret)
    }

    ctx.setOAuthCredentials(params, new OAuthHmacSha1Signer())

    // set token
    ctx.setOAuthCredentials(params, new OAuthHmacSha1Signer());

    // oauth step 1
    def getRequestToken(helper:GoogleOAuthHelper, params:GoogleOAuthParameters) = {
      import scala.actors.Actor._

      // set default params
      val scope = scopeURL match {
        case null => "http://spreadsheets.google.com/feeds/"
        case x => x
      }

      // set params
      params.setScope(scope)
      params.setOAuthCallback("http://127.0.0.1:" ++ port.toString ++ "/")
      helper.getUnauthorizedRequestToken(params);
    }

    // oauth step 2
    def getAuthorizeToken(helper:GoogleOAuthHelper, params:GoogleOAuthParameters) = {
      import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
      import java.net.InetSocketAddress
      import java.util.concurrent.Executor
      import java.io._

      var token:String = null
      val server = HttpServer.create(new InetSocketAddress(port), 0)

      object handler extends HttpHandler {
        def handle(http: HttpExchange) {

          // read body
          val s = http.getRequestURI.toString
          val queryStr = s.slice(s.indexOf('?') + 1, s.size)
          helper.getOAuthParametersFromCallback(queryStr, params);
          token = helper.getAccessToken(params)
          println("Got access token: " ++ token)
          http.getRequestBody().close()

          // response
          val out = new OutputStreamWriter(http.getResponseBody())
          out.write("OK. Close this page.")
          out.close()
          http.close()
        }
      }

      server.createContext("/", handler)
      server.start()

      // print approval page URL
      val url = helper.createUserAuthorizationUrl(params)
      try {
        import scala.sys.process.Process
        Process("open " + url).run
      } catch { case e:Exception =>
        println("Visit the following URL with your browser to get access token:")
        println(url)
      }


      while (token == null) { Thread.sleep(1000) }

      // stop server
      val timeout = 5
      server.stop(timeout)
    }

    // oauth step 3
    def saveAccessToken(file:File, params:GoogleOAuthParameters) = {
      val token  = params.getOAuthToken()
      val secret = params.getOAuthTokenSecret()

      if (file.exists()) { file.delete() }

      val writer = new PrintWriter(file)
      try {
        writer.println(token)
        writer.println(secret)
      } finally { writer.close() }
    }
  }
}
