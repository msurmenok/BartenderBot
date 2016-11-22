package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Message, RecipientInfo}


trait ResponseGenerator {
  def generateResponse(recipientMessage: Message,
                       recipientInfo: Option[RecipientInfo]): RecipientInfo
}

class SimpleResponseGenerator extends ResponseGenerator {

  val GREETING_MESSAGE = Message("Hi, how are you?")
  val DRUNK_MESSAGE = Message("Are you drunk ? ;)")
  val WANT_DRINK_QUESTION = Message("Do you want something to drink?")
  val NAME_QUESTION = Message("What is your name?")
  val INFO_MESSAGE = Message("I can help you to find best bar. " +
    "However, if you wanna create home party, I'll know a lot of cocktail's receipts ;)")
  val IF_YOU_NOT_DRINK_INFO_MESSAGE = Message("I guess you don't wanna drink. " +
    "But I know also a lot of best restaurants whit very testy food and no-alcoholic cocktail's receipts ;)")

  def generateResponse(recipientMessage: Message,
                       recipientInfo: Option[RecipientInfo]): RecipientInfo = {

    if (recipientInfo.isEmpty) {
      return RecipientInfo(lastResponse = GREETING_MESSAGE)
    }

    var info = recipientInfo.get

    info.lastResponse match {
      case WANT_DRINK_QUESTION =>
        info = info.copy(likeDrink = Some(isYes(recipientMessage.text)))
      case NAME_QUESTION =>
        val recipientName = getName(recipientMessage.text)
        return getMeetingMessage(recipientName, info)
      case _ =>
    }

    if (isGreeting(recipientMessage.text)) {
      if (info.lastResponse.equals(GREETING_MESSAGE)) {
        info.copy(lastResponse = DRUNK_MESSAGE)
      } else {
        info.copy(lastResponse = GREETING_MESSAGE)
      }
    } else if (info.likeDrink.isEmpty) {
      info.copy(lastResponse = WANT_DRINK_QUESTION)
    } else if (info.name.isEmpty) {
      info.copy(lastResponse = NAME_QUESTION)
    } else if (!info.lastResponse.text.contains(IF_YOU_NOT_DRINK_INFO_MESSAGE.text)
      && !info.lastResponse.text.contains(INFO_MESSAGE.text)) {
      createInfoMessage(info)
    } else {
      info.copy(lastResponse = Message(s"I receive you message: ${recipientMessage.text}. I'm finding answer."))
    }
  }

  private def getName(text: String): Option[String] = {
    //todo identification name
    Some(text)
  }

  private def isGreeting(textMessage: String): Boolean = {
    val text = textMessage.toLowerCase()
    Seq("hello", "hi", "good morning", "good evening", "good afternoon").count(text.contains) > 0
  }

  private def isYes(textMessage: String): Boolean = {
    val text = textMessage.toLowerCase()
    Seq("y", "of course", "sure", "absolutely", "certainly").count(text.contains) > 0
  }

  private def getMeetingMessage(recipientName: Option[String], info: RecipientInfo) = {
    val meetingMessageText = recipientName match {
      case None => "OK B-)"
      case _ => s"Nice to meet you ${recipientName.get}. I’m “Bartender"
    }
    val infoMessage = createInfoMessage(info)

    infoMessage.copy(name = recipientName,
      lastResponse = Message(s"$meetingMessageText ${infoMessage.lastResponse.text}"))
  }

  private def createInfoMessage(info: RecipientInfo): RecipientInfo = {
    info.likeDrink match {
      case Some(true) => info.copy(lastResponse = INFO_MESSAGE)
      case Some(false) => info.copy(lastResponse = IF_YOU_NOT_DRINK_INFO_MESSAGE)
      case None => info
    }
  }

}
