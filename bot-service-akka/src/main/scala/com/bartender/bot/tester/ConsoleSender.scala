package com.bartender.bot.tester

import com.bartender.bot.service.domain._
import com.bartender.bot.service.services.MessageSender

class ConsoleSender extends MessageSender {
  def sendMessage(message: Message, recipient: Recipient): Unit = {
    println("Bot: " + message.text)
  }

  def sendNearestBar(bar: Bar, recipient: Recipient, location: Location, offset: Int): Unit = {
    println(s"Bot: $offset ${bar.name} ${if (bar.address.isDefined) s"(${bar.address.get})" else ""} " +
      s"\n for load more input: next" +
      s"\n for details: details")
  }

  def sendBarDetails(barDetails: BarDetails, recipient: Recipient): Unit = {
    println(s"Bot: website: ${barDetails.website.getOrElse("-")}" +
      s"\n phone number: ${barDetails.phoneNumber.getOrElse("-")}" +
      s"\n rating: ${barDetails.rating.map(_.toString).getOrElse("-")}" +
      s"\n price level: ${barDetails.priceLevelToStr()}" +
      s"\n reviews: ${barDetails.reviews.mkString("\n       -")}" +
      s"\n\n for find other one bar input: next")
  }

  def sendCocktailList(cocktails: Seq[Cocktail], recipient: Recipient, alcohol: String): Unit = {
    println(s"Bot: cocktails: ${cocktails.map(_.name).mkString("\n       -")}")
  }

  def sendCocktailReceipt(cocktail: Cocktail, cocktailReceipt: CocktailReceipt, recipient: Recipient): Unit = {
    println(s"Bot: cocktail: ${cocktail.name}" +
      s"\n glass: ${cocktailReceipt.glass.getOrElse("-")}" +
      s"\n instruction: ${cocktailReceipt.instruction.map(_.toString).getOrElse("-")}" +
      s"\n reviews: ${cocktailReceipt.ingredients.mkString("\n       -")}")
  }
}
