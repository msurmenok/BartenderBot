package com.bartender.bot.tester

import com.bartender.bot.service.domain.{Message, Recipient}
import com.bartender.bot.service.services.MessageSender

class ConsoleSender extends MessageSender {
  override def SendMessage(message: Message, recipient: Recipient): Unit = {
    println("Bot: " + message.text)
  }
}
