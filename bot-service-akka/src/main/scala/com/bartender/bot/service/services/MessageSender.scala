package com.bartender.bot.service.services

import com.bartender.bot.service.domain._

trait MessageSender {
  def sendMessage(message: Message, recipient: Recipient)

  def sendNearestBar(bar: Bar, recipient: Recipient, location: Location, offset: Int)

  def sendBarDetails(barDetails: BarDetails, recipient: Recipient)

  def sendCocktailList(cocktails: Seq[Cocktail], recipient: Recipient, alcohol: String, offset: Int)

  def sendCocktailReceipt(cocktail: Cocktail, cocktailReceipt: CocktailReceipt, recipient: Recipient)
}
