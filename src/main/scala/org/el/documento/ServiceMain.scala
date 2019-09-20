package org.el.documento

import io.sentry.Sentry

object ServiceMain {
  def main(args: Array[String]): Unit = {
    Sentry.init("https://94c76c9e354548df88579621dba60fc9@sentry.io/1738831")

    try
      {
        print("hello")
    }
    catch {
      case e: Exception =>
        Sentry.capture(e)
    }
  }
}
