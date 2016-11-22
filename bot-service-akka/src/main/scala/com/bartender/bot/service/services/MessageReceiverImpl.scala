package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Message, Recipient, RecipientInfo}

class MessageReceiverImpl(sender: MessageSender, dao: Dao,
                          responseGenerator: ResponseGenerator) extends MessageReceiver {

  override def Receive(message: Message, recipient: Recipient) {

    dao.saveRecipientMessage(message)

    val recipientInfo = dao.getRecipientInfo(recipient)

    val updatedRecipientInfo = responseGenerator.generateResponse(message, recipientInfo)

    dao.saveResponse(updatedRecipientInfo.lastResponse)
    dao.saveOrUpdateRecipientInfo(recipient, updatedRecipientInfo)

    sender.SendMessage(updatedRecipientInfo.lastResponse, recipient)
  }
}
