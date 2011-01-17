import org.jibble.pircbot.PircBot

object Scalabot extends PircBot {
  def main(args: Array[String]) {
    setName("scalabot")
    setVerbose(true)
    setEncoding("UTF-8")
    connect()
  }

  def connect() {
    connect("irc.freenode.net")
    identify("password")
    joinChannel("#scala")
    joinChannel("#functionaljava")
    joinChannel("#learnanycomputerlanguage")
    joinChannel("##jvm")
    joinChannel("#scalatra")
  }

  override def onDisconnect {
    while (true)
      try {
        connect()
        return
      }
      catch { case e: Exception =>
        e.printStackTrace
        Thread sleep 30000
      }
  }

  override def onPrivateMessage(sender: String, login: String, hostname: String, message: String) {
    onMessage(sender, sender, login, hostname, getName() + ": " + message)
  }

  override def onMessage(channel: String, sender: String, login: String, hostname: String, message: String) {
    import java.io._
    import java.net._
    println("Channel = " + channel)
    if (sender == "lambdabot" || sender == "lambdac")
      return
    if (message.startsWith(getName() + ": ")) {
      //2-8.latest.scala-tweets.appspot.com/interp?bot=irc&code=...
      val url = new URL("http://www.simplyscala.com/interp?bot=irc&code=" + URLEncoder.encode(message.substring((getName + ": ").length), "UTF-8"))
      val reader = new BufferedReader(new InputStreamReader(url.openConnection.getInputStream, "UTF-8"))
      try {
        for (i <- 1 to 2) {
          var line = reader.readLine
          println("line = " + line)
          if (line == "warning: there were deprecation warnings; re-run with -deprecation for details") line = reader.readLine
          if (line == "warning: there were unchecked warnings; re-run with -unchecked for details") {
            sendMessage(channel, line);
            line = reader.readLine
          }
          if (line == "New interpreter instance being created for you, this may take a few seconds." || line == "Please be patient.") {
            onMessage(channel, sender, login, hostname, message)
            return
          }
          sendMessage(channel, line.replaceAll("^res[0-9]+: ", ""))
        }
      }
      finally {
        reader.close
      }
    }
    if (message.contains(" #") || message.startsWith("#") && !getUsers(channel).exists(_.getNick == "scala-tracbot")) {
      val number = message.replaceAll("^[^#]*#", "").replaceAll("([^0-9].*)?", "")
      if (number.length >= 3) {
        val url = "http://lampsvn.epfl.ch/trac/scala/ticket/" + number
        if (!url.endsWith("/"))
          sendMessage(channel, "Perhaps you meant " + url)
      }
    }

    if (message.contains(" r") || message.startsWith("r")) {
      val number = message.replaceAll("^[^r]*r", "").replaceAll("([^0-9].*)?", "")
      if (number.length >= 5) {
        val url = "http://lampsvn.epfl.ch/trac/scala/changeset/" + number
        if (!url.endsWith("/"))
          sendMessage(channel, "That revision can be found at " + url)
        val reader = new BufferedReader(new InputStreamReader(new URL(url).openConnection.getInputStream, "UTF-8"))
        var line = reader.readLine
        while (line != null) {
          println(line)
          line = reader.readLine
        }
      }
    }
  }
}
