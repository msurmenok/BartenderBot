package com.bartender.bot.service.fb

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, StandardRoute, ValidationRejection}
import com.bartender.bot.service.common.{Config, Logging}
import com.bartender.bot.service.domain.{Message, Recipient}
import com.bartender.bot.service.services.MessageReceiver

class FbMessengerService(receiver: MessageReceiver) extends Directives with JsonSupport with Config with Logging {
  val route = path(fbMessengerWebhook) {
    get {
      parameters("hub.mode", "hub.verify_token" ?, "hub.challenge" ?) {
        validateWebhook
      }
    } ~
      post {
        entity(as[FbMessengerHookBody]) { hookBody =>
          rootLogger.info(s"$hookBody")
          val userMessage = hookBody.entry.last.messaging.last.message

          if (userMessage.isDefined) {
            val messageTextOption = userMessage.get.text
            if (messageTextOption.isDefined) {
              val message = Message(messageTextOption.get)
              val recipient = Recipient(hookBody.entry.last.messaging.last.sender.id)

              receiver.Receive(message, recipient)
            }
          }

          complete("Ok")
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
