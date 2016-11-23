package com.bartender.bot.service.fb

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
        extractRequestEntity { response =>
          rootLogger.info(s"$response") // it more usefull for log now todo late we can delete it

          entity(as[FbMessengerHookBody]) { hookBody =>
            if (hookBody.`object` matches "page") hookBody.entry.foreach(
              //just text message for first time todo support all message type
              _.messaging.filter(fbMessaging => fbMessaging.message.isDefined && fbMessaging.message.get.text.isDefined)
                .map(messaging => (messaging.message.get, messaging.sender.id))
                .foreach { fbMessage =>
                  val message = Message(fbMessage._1.text.get)
                  val recipient = Recipient(fbMessage._2)
                  receiver.Receive(message, recipient)
                })

            complete("Success")
          }
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
