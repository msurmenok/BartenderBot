package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Location, Message, Recipient, RecipientInfo}

class MessageReceiverImpl(sender: MessageSender, dao: Dao,
                          responseGenerator: ResponseGenerator,
                          barResearcher: BarResearcher) extends MessageReceiver {

  override def Receive(message: Message, recipient: Recipient) {

    dao.saveRecipientMessage(message)

    val recipientInfo = dao.getRecipientInfo(recipient)

    val updatedRecipientInfo = responseGenerator.generateResponse(message, recipientInfo)

    dao.saveResponse(updatedRecipientInfo.lastResponse)
    dao.saveOrUpdateRecipientInfo(recipient, updatedRecipientInfo)

    sender.SendMessage(updatedRecipientInfo.lastResponse, recipient)
  }

  override def receiveNearestBars(location: Location, recipient: Recipient): Unit = {
    // TODO: send message with attachment
    barResearcher.findNearestBars(location).map(bar => Message(s"${bar.name}: ${bar.photoUrl.getOrElse("")}"))
      .foreach(message => sender.SendMessage(message, recipient))
  }
}
