package com.bartender.bot.service.fb

import akka.http.scaladsl.server.{Directives, StandardRoute, ValidationRejection}
import com.bartender.bot.service.common.{Config, Logging}


object FbMessengerService extends Directives with JsonSupport with Config with Logging {
  val route = path(fbMessengerWebhook) {
    get {
      parameters("hub.mode", "hub.verify_token" ?, "hub.challenge" ?) {
        validateWebhook
      }
    } ~
      post {
        entity(as[FbMessengerResponse]) { response =>
          val userMessage = response.entry.head.messaging.head.message.text.head
          rootLogger.info(s"euser message: $userMessage")
          complete(userMessage)
        }
      }
  }

  private def validateWebhook: (String, Option[String], Option[String]) => StandardRoute = {
    (mode, token, challenge) =>
      if (mode == "subscribe" && token.getOrElse("").equals(fbMessengerVerifyToken)) {
        rootLogger.info("Validating webhook")
        complete(challenge)
      } else {
        rootLogger.error("Failed validation. Make sure the validation tokens match.")
        reject(ValidationRejection("Verify token not correct!"))
      }
  }
}
