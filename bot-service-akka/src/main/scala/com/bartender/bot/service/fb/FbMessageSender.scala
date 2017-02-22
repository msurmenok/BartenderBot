package com.bartender.bot.service.fb

import com.bartender.bot.service.domain._
import com.bartender.bot.service.services.MessageSender

import scala.collection.mutable.ListBuffer

class FbMessageSender(val fbMessengerSendApiClient: FbMessengerSendApiClient = new FbMessengerSendApiClient())
  extends MessageSender {

  val max_message_leght = 320
  val max_templete_text_leght = 80

  def sendMessage(message: Message, recipient: Recipient): Unit = {
    val text = if (message.text.length() > max_message_leght) {
      val dots = "..."
      message.text.substring(0, max_message_leght - dots.length - 1) + dots
    } else {
      message.text
    }
    fbMessengerSendApiClient.sendTextMessage(FbRecipient(recipient.id), text)
  }

  def sendNearestBar(bar: Bar, recipient: Recipient, location: Location, offset: Int): Unit = {
    val buttons = Seq(FbActionButtons.barDetails(bar.id), FbActionButtons.barShowNext(location, offset))

    val element = FbTemplateElement(
      title = bar.name,
      subtitle = bar.address,
      image_url = bar.photoUrl,
      buttons = buttons)

    fbMessengerSendApiClient.sendTemplateMessage(FbRecipient(recipient.id), element)
  }

  def sendBarDetails(barDetails: BarDetails, recipient: Recipient): Unit = {
    val ratingAndPriceTitle = s"rating: ${barDetails.rating.map(_.toString).getOrElse("-")}," +
      s" price level: ${priceLevelToStr(barDetails.priceLevel)}"
    val titles = new ListBuffer[String]
    titles += ratingAndPriceTitle
    if (barDetails.reviews.nonEmpty) {
      titles += "Reviews: "
      titles ++= barDetails.reviews
        .map { review =>
          val preffix = "  - "
          val max_text_leght = max_templete_text_leght - preffix.length
          if (review.length() > max_text_leght) {
            val dots = "..."
            preffix + review.substring(0, max_text_leght - dots.length - 1) + dots
          } else {
            preffix + review
          }
        }
    }

    val buttons = new ListBuffer[FbTemplateButtons]
    if (barDetails.website.nonEmpty) {
      buttons += FbTemplateButtons(FbTemplateButtonsType.web_url, title = "Go to website", url = Some(barDetails.website.get))
    }

    if (barDetails.phoneNumber.nonEmpty) {
      buttons += FbTemplateButtons(FbTemplateButtonsType.phone_number, title = "Call to bar", payload = Some(barDetails.phoneNumber.get))
    }

    fbMessengerSendApiClient.sendListTemplateMessage(FbRecipient(recipient.id),
      titles.map(title => FbTemplateElement(title)),
      if (buttons.isEmpty) None else Some(buttons.toList))
  }
}
