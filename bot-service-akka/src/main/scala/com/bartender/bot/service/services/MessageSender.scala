package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Message, Recipient}

trait MessageSender {
  def SendMessage(message: Message, recipient: Recipient)
}
