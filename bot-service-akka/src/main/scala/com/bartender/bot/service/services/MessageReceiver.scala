package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Bar, Location, Message, Recipient}

trait MessageReceiver {
  def receive(message: Message, recipient: Recipient)

  def receiveNearestBar(location: Location, recipient: Recipient, offset: Int = 0): Option[Bar]

  def receiveBarDetails(barId: String, recipient: Recipient)
}
