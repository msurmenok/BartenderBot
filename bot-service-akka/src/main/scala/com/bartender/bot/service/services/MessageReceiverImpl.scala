package com.bartender.bot.service.services

import com.bartender.bot.service.domain._

class MessageReceiverImpl(sender: MessageSender,
                          responseGenerator: ResponseGenerator,
                          barResearcher: BarResearcher) extends MessageReceiver {

  def receive(message: Message, recipient: Recipient) {

    val response = responseGenerator.generateResponse(message, recipient)

    sender.sendMessage(response.message, recipient)

    response.action match {
      case Some(action) => action match {
        case BotActions.COCKTAIL_BY_ALCOHOL => //todo
        case BotActions.COCKTAIL_RANDOM => //todo
        case BotActions.NEAREST_BAR => //todo: now waiting location, may be can find by city
        case BotActions.INPUT_WELCOME => //todo: may be something tracking
      }
    }
  }

  def receiveNearestBar(location: Location, recipient: Recipient, offset: Int = 0): Option[Bar] = {

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

  def receiveBarDetails(barId: String, recipient: Recipient): Unit = {
    barResearcher.getBarDetails(barId) match {
      case Some(details) => sender.sendBarDetails(details, recipient)
      case None => sender.sendMessage(Message("I look everywhere, really! Nothing can't find!"), recipient)
    }
  }
}
