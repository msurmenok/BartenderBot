package com.bartender.bot.service.services

import com.bartender.bot.service.domain._

trait MessageSender {
  def sendMessage(message: Message, recipient: Recipient)

  def sendNearestBar(bar: Bar, recipient: Recipient, location: Location, offset: Int)

  def sendBarDetails(barDetails: BarDetails, recipient: Recipient)

  def priceLevelToStr(priceLevel: Option[Int]): String = {
    val str = (0 to priceLevel.getOrElse(0)).map(_ => "$").mkString("")
    if (priceLevel.isEmpty) {
      "-"
    }
    else {
      str
    }
  }
}
