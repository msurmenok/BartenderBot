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
          val userMessage = response.entry.head.messaging.head.message
          val result: String = userMessage.text match {
            case Some(text) => text
            case None =>
              val attachment = userMessage.attachments.get.head
              attachment.`type` match {
                case AttachmentType.location => attachment.payload.coordinates.head.toString
                case _ => attachment.payload.url.get
              }
          }
          rootLogger.info(s"user message: $userMessage")
          FbMessengerSendApiClient.sendTextMessage(response.entry.head.messaging.head.recipient, result)
          complete(result)
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
