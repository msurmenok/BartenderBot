package com.bartender.bot.service.fb

import com.bartender.bot.service.domain.Message
import com.bartender.bot.service.domain.Recipient
import com.bartender.bot.service.services.MessageSender

class FbMessageSender extends MessageSender {
  def SendMessage(message: Message, recipient: Recipient): Unit =
  {
    FbMessengerSendApiClient.sendTextMessage(FbRecipient(recipient.id), message.text)
  }
}
