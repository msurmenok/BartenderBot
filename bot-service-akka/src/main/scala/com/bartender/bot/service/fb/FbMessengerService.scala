package com.bartender.bot.service.fb

import akka.http.scaladsl.server.{Directives, StandardRoute, ValidationRejection}
import com.bartender.bot.service.common.{Config, Logging}
import com.bartender.bot.service.domain.{Location, Message, Recipient}
import com.bartender.bot.service.services.MessageReceiver

class FbMessengerService(receiver: MessageReceiver) extends Directives with FbJsonSupport with Config with Logging {
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
              _.messaging.foreach { messaging =>
                val recipient = Recipient(messaging.sender.id)
                if (messaging.message.isDefined) {
                  val message = messaging.message.get
                  val coordinates = message.attachments.map { attachments =>
                    attachments.find(_.`type` == FbAttachmentType.location)
                      .map(_.payload.coordinates).getOrElse(None)
                  }.getOrElse(None)

                  if (coordinates.isDefined) {
                    val location = Location(coordinates.get.lat, coordinates.get.long)
                    receiver.receiveNearestBar(location, recipient)
                  } else if (message.text.isDefined) {
                    receiver.receive(Message(message.text.get), recipient)
                  }
                } else if (messaging.postback.isDefined) {
                  val payload = messaging.postback.get.payload
                  if (payload.startsWith(FbActionButtons.SHOW_NEXT_BAR)) {
                    val args = FbActionButtons.getBarShowNextPayload(payload)
                    receiver.receiveNearestBar(args._1, recipient, args._2)
                  } else if (payload.startsWith(FbActionButtons.BAR_DETAILS)) {
                    val barId = FbActionButtons.getBarDetailsPayload(payload)
                    receiver.receiveBarDetails(barId, recipient)
                  } else {
                    receiver.receive(Message(s"Don't understand posback payload: $payload"), recipient)
                  }
                } else {
                  val typeEntity = messaging.delivery.map(_ => "delivery").getOrElse("") + messaging.read.map(_ => "read").getOrElse("")
                  receiver.receive(Message(s"Not support entity $typeEntity type. Now support message and postback."), recipient)
                }
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
