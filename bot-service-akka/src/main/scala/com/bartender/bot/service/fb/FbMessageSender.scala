package com.bartender.bot.service.fb

import com.bartender.bot.service.domain._
import com.bartender.bot.service.services.MessageSender

import scala.collection.mutable.ListBuffer

class FbMessageSender(val fbMessengerSendApiClient: FbMessengerSendApiClient = new FbMessengerSendApiClient())
  extends MessageSender {

  val max_message_leght = 320
  val max_templete_text_leght = 80
  val max_list_template_elements_count = 4
  val min_list_template_elements_count = 2

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
      buttons = Some(buttons))

    fbMessengerSendApiClient.sendTemplateMessage(FbRecipient(recipient.id), element)
  }

  def sendBarDetails(barDetails: BarDetails, recipient: Recipient): Unit = {

    val templates = new ListBuffer[FbTemplateElement]()
    templates += FbTemplateElement(
      title = s"Rating: ${barDetails.rating.map(_.toString).getOrElse("-")}",
      subtitle = Some(s"Price level: ${priceLevelToStr(barDetails.priceLevel)}"),
      image_url = barDetails.extraPhotoUrl,
      default_action = barDetails.website.map(url => FbTemplateElementDefaultAction(url = url)))

    if (barDetails.reviews.isEmpty) {
      templates += FbTemplateElement("Reviews", subtitle = Some("empty"))
    } else {
      templates ++= barDetails.reviews.take(max_list_template_elements_count - templates.length)
        .map { review =>
          val preffix = "  - "
          val max_text_leght = max_templete_text_leght - preffix.length
          if (review.text.length() > max_text_leght) {
            val dots = "..."
            (review.author, preffix + review.text.substring(0, max_text_leght - dots.length - 1) + dots)
          } else {
            (review.author, preffix + review)
          }
        }.map(title => FbTemplateElement(title._1, subtitle = Some(title._2)))
    }

    fbMessengerSendApiClient.sendListTemplateMessage(
      FbRecipient(recipient.id),
      templates.toList,
      barDetails.phoneNumber.map(phone =>
        FbTemplateButton(FbTemplateButtonsType.phone_number, title = "Call to bar", payload = Some(phone))))
  }
}
