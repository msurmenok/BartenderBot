package com.bartender.bot.service.fb

import com.bartender.bot.service.domain.{Message, Recipient}
import com.bartender.bot.service.services.MessageSender

class FbMessageSender(val fbMessengerSendApiClient: FbMessengerSendApiClient = new FbMessengerSendApiClient())
  extends MessageSender {

  def SendMessage(message: Message, recipient: Recipient): Unit = {
    fbMessengerSendApiClient.sendTextMessage(FbRecipient(recipient.id), message.text)
  }
}
