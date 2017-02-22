package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Bar, Location, Message, Recipient}

class MessageReceiverImpl(sender: MessageSender, dao: Dao,
                          responseGenerator: ResponseGenerator,
                          barResearcher: BarResearcher) extends MessageReceiver {

  override def receive(message: Message, recipient: Recipient) {

    dao.saveRecipientMessage(message)

    val recipientInfo = dao.getRecipientInfo(recipient)

    val updatedRecipientInfo = responseGenerator.generateResponse(message, recipientInfo)

    dao.saveResponse(updatedRecipientInfo.lastResponse)
    dao.saveOrUpdateRecipientInfo(recipient, updatedRecipientInfo)

    sender.sendMessage(updatedRecipientInfo.lastResponse, recipient)
  }

  override def receiveNearestBar(location: Location, recipient: Recipient, offset: Int = 0): Option[Bar] = {

    val bars = barResearcher.findNearestBars(location)

    if (bars.isEmpty) {
      sender.sendMessage(Message("Can't find nearest bars :( You're in a hole! Go away from there!"), recipient)
      None
    } else if (offset >= bars.size) {
      sender.sendMessage(Message("Can't find one more nearest bars :( Сhoose from a previous!"), recipient)
      None
    } else {
      val newOffset = offset + 1
      val nearestBar = bars.drop(offset).head
      sender.sendNearestBar(nearestBar, recipient, location, newOffset)
      Some(nearestBar)
    }

  }

  override def receiveBarDetails(barId: String, recipient: Recipient): Unit = {
    barResearcher.getBarDetails(barId) match {
      case Some(details) => sender.sendBarDetails(details, recipient)
      case None => sender.sendMessage(Message("I look everywhere, really! Nothing can't find!"), recipient)
    }
  }
}
