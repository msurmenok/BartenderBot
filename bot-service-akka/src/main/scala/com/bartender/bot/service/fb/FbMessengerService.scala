package com.bartender.bot.service.fb

import akka.http.scaladsl.server.{Directives, StandardRoute, ValidationRejection}
import com.bartender.bot.service.common.{Config, Logging}
import com.bartender.bot.service.domain.{Location, Message, Recipient}
import com.bartender.bot.service.fb.FbActionButtons.{BarDetailsButton, CocktailReceiptButton, ShowMoreCocktailsButton, ShowNexBarButton}
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
                if (messaging.postback.isDefined) {
                  handlePostback(messaging.postback.get, recipient)
                } else if (messaging.message.isDefined) {
                  handleMessage(messaging.message.get, recipient)
                } else {
                  val typeEntity = messaging.delivery.map(_ => "delivery").getOrElse("") + messaging.read.map(_ => "read").getOrElse("")
                  rootLogger.debug(s"Not support entity $typeEntity type. Now support message and postback.")
                }
              })

            complete("Success")
          }
        }

      }
  }

  private def handlePostback(postback: FbButtonPostback, recipient: Recipient): Any = {
    FbActionButtons.Factory.create(postback.payload) match {
      case Some(button) => button match {
        case ShowNexBarButton(location, offset) => receiver.receiveNearestBar(location, recipient, offset)
        case BarDetailsButton(barId) => receiver.receiveBarDetails(barId, recipient)
        case CocktailReceiptButton(cocktailId) => receiver.receiveCocktailReceipt(cocktailId, recipient)
        case ShowMoreCocktailsButton(alcohol, offset) => receiver.receiveCoctailsByAlcohol(alcohol, recipient, offset)
      }
      case None => rootLogger.debug(s"Don't understand posback payload: ${postback.payload}")
    }
  }

  private def handleMessage(message: FbMessage, recipient: Recipient): Any = {
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
