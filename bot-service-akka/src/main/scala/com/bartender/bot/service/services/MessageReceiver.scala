package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Message, Recipient}

trait MessageReceiver {
  def Receive(message: Message, recipient: Recipient)
}
