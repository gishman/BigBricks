package com.homedepot.bigbricks.ui


/**
  * Created by Ferosh Jacob on 11/28/16.
  */
object SlackNotification extends BigBricksLogging{






  def notifyInOrangeGraph(message:String) = {

    try {
      val json= s"""
        {
        "text":"$message",
         "username":"BigBricks",
         "icon_emoji":":building_construction:",
         "icon_url":"http://search.olt-homedepot.com/bigbricks"
         }
      """

      System.getProperty("SLACK_ORANGEGRAPH") match {
        case "" => logger.error("SLACK URL not set!")
        case null => logger.error("SLACK URL not set!")
        case url: String => {
          logger.info("Slack notification:" + message)
          SlackNotify.notify(json, url)
        }
      }
    }catch {
      case x:Throwable => x.printStackTrace()
    }

  }

}
