package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Location, Message, Recipient}

trait MessageReceiver {
  def Receive(message: Message, recipient: Recipient)

  def receiveNearestBars(location: Location, recipient: Recipient)
}
