package com.bartender.bot.service.fb

import com.bartender.bot.service.domain._
import com.bartender.bot.service.fb.FbActionButtons.{BarDetailsButton, CocktailReceiptButton, ShowMoreCocktailsButton, ShowNexBarButton}
import com.bartender.bot.service.services.MessageSender

import scala.collection.mutable.ListBuffer

class FbMessageSender(val fbMessengerSendApiClient: FbMessengerSendApiClient)
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
    val buttons = Seq(BarDetailsButton(bar.id), ShowNexBarButton(location, offset)).map(FbActionButtons.createFbTemplateButton)

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
      subtitle = Some(s"Price level: ${barDetails.priceLevelToStr()}"),
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

  def sendCocktailList(cocktails: Seq[Cocktail], recipient: Recipient, alcohol: String, offset: Int): Unit = {
    val newOffset = offset + max_list_template_elements_count
    val fbActionButton = if (newOffset < cocktails.size) {
      Some(FbActionButtons.createFbTemplateButton(ShowMoreCocktailsButton(alcohol, newOffset)))
    } else {
      None
    }

    val fbTemplateElements = cocktails.slice(offset, newOffset).map(cocktail =>
      FbTemplateElement(
        title = cocktail.name,
        image_url = cocktail.imageUrl,
        buttons = Some(Seq(
          FbActionButtons.createFbTemplateButton(CocktailReceiptButton(cocktail.id))
        )))
    )

    fbMessengerSendApiClient.sendListTemplateMessage(
      FbRecipient(recipient.id),
      fbTemplateElements,
      fbActionButton
    )
  }

  def sendCocktailReceipt(cocktail: Cocktail, cocktailReceipt: CocktailReceipt, recipient: Recipient): Unit = {
    val element = FbTemplateElement(
      title = cocktail.name,
      subtitle = cocktailReceipt.glass,
      image_url = cocktail.imageUrl)

    fbMessengerSendApiClient.sendTemplateMessage(FbRecipient(recipient.id), element)

    cocktailReceipt.instruction.foreach(instruction =>
      sendMessage(Message(instruction), recipient))

    if (cocktailReceipt.ingredients.nonEmpty) {
      val ingredients = cocktailReceipt.ingredients.map(ingredient =>
        s"- ${ingredient.name} (${ingredient.measure})").mkString("Ingredients: \n", "\n", "")
      sendMessage(Message(ingredients), recipient)
    }
  }
}
