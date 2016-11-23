package com.bartender.bot.service

import com.bartender.bot.service.domain.{Message, RecipientInfo}
import org.scalatest.{FlatSpec, Matchers}
import com.bartender.bot.service.services.SimpleResponseGenerator

class SimpleResponseGeneratorTest extends FlatSpec with Matchers {

  val responseGenerator = new SimpleResponseGenerator()

  "random first user message" should "return greeting message" in {

    val actual = responseGenerator.generateResponse(Message("random"), None)

    actual.lastResponse shouldEqual responseGenerator.GREETING_MESSAGE
    actual.name shouldEqual None
    actual.likeDrink shouldEqual None
  }

  "first HI user message" should "return greeting message" in {

    val actual = responseGenerator.generateResponse(Message("HI"), None)

    actual.lastResponse shouldEqual responseGenerator.GREETING_MESSAGE
    actual.name shouldEqual None
    actual.likeDrink shouldEqual None
  }

  "Not first GOOD EVENING user message" should "return greeting message" in {

    val userName = Some("Ivan")
    val userLikeDrink = Some(true)

    val actual = responseGenerator.generateResponse(Message("GOOD EVENING"),
      Some(RecipientInfo(name = userName, likeDrink = userLikeDrink,
        lastResponse = Message("random"))))

    actual.lastResponse shouldEqual responseGenerator.GREETING_MESSAGE
    actual.name shouldEqual userName
    actual.likeDrink shouldEqual userLikeDrink
  }

  "When bot don't know about like user drink or not, bot" should "ask it" in {

    val actual = responseGenerator.generateResponse(Message("random"),
      Some(RecipientInfo(lastResponse = Message("random"))))

    actual.lastResponse shouldEqual responseGenerator.WANT_DRINK_QUESTION
    actual.name shouldEqual None
    actual.likeDrink shouldEqual None
  }

  "When bot don't know user name, but know about like user drink or not, bot" should "ask user name" in {
    val userLikeDrink = Some(true)

    val actual = responseGenerator.generateResponse(Message("random"),
      Some(RecipientInfo(likeDrink = userLikeDrink, lastResponse = Message("random"))))

    actual.lastResponse shouldEqual responseGenerator.NAME_QUESTION
    actual.name shouldEqual None
    actual.likeDrink shouldEqual userLikeDrink
  }

  "When user say his name, bot" should "return meeting message" in {

    val recipientMessage = Message("name")

    val actual = responseGenerator.generateResponse(recipientMessage,
      Some(RecipientInfo(lastResponse = responseGenerator.NAME_QUESTION)))

    actual.lastResponse.text.contains(actual.name.get) shouldEqual true
    actual.name.get shouldEqual recipientMessage.text
    actual.likeDrink shouldEqual None
  }

  "When user say about like he drink or no, bot" should "identifications it" in {
    val recipientMessage = Message("yes")

    val actual = responseGenerator.generateResponse(recipientMessage,
      Some(RecipientInfo(lastResponse = responseGenerator.WANT_DRINK_QUESTION)))

    actual.lastResponse shouldEqual responseGenerator.NAME_QUESTION
    actual.name shouldEqual None
    actual.likeDrink shouldEqual Some(true)
  }

  "When bot know user name and about like user drink and user not send HELLO" should "return info message" in {

    val userName = Some("Ivan")
    val userLikeDrink = Some(true)
    val userNotLikeDrink = Some(false)

    val actual1 = responseGenerator.generateResponse(Message("random"),
      Some(RecipientInfo(name = userName, likeDrink = userLikeDrink,
        lastResponse = Message("random"))))
    val actual2 = responseGenerator.generateResponse(Message("random"),
      Some(RecipientInfo(name = userName, likeDrink = userNotLikeDrink,
        lastResponse = Message("random"))))

    actual1.lastResponse shouldEqual responseGenerator.INFO_MESSAGE
    actual1.name shouldEqual userName
    actual1.likeDrink shouldEqual userLikeDrink

    actual2.lastResponse shouldEqual responseGenerator.IF_YOU_NOT_DRINK_INFO_MESSAGE
    actual2.name shouldEqual userName
    actual2.likeDrink shouldEqual userNotLikeDrink
  }

  "When bot know user name and about like user drink and user not send HELLO" +
    " and last response is info message" should "return echо message" in {

    val randomMessage = Message("random")
    val userName = Some("Ivan")
    val userLikeDrink = Some(true)


    val actual1 = responseGenerator.generateResponse(randomMessage,
      Some(RecipientInfo(name = userName, likeDrink = userLikeDrink,
        lastResponse = responseGenerator.INFO_MESSAGE)))

    val actual2 = responseGenerator.generateResponse(randomMessage,
      Some(RecipientInfo(name = userName, likeDrink = userLikeDrink,
        lastResponse = responseGenerator.IF_YOU_NOT_DRINK_INFO_MESSAGE)))


    actual1.lastResponse.text.contains(randomMessage.text) shouldEqual true
    actual1.name shouldEqual userName
    actual1.likeDrink shouldEqual userLikeDrink

    actual2.lastResponse.text.contains(randomMessage.text) shouldEqual true
    actual2.name shouldEqual userName
    actual2.likeDrink shouldEqual userLikeDrink
  }

  "When user send HELLO second time" should "return DRUNK_MESSAGE" in {
    val userName = Some("Ivan")
    val userLikeDrink = Some(true)

    val actual = responseGenerator.generateResponse(Message("Hello"),
      Some(RecipientInfo(name = userName, likeDrink = userLikeDrink,
        lastResponse = responseGenerator.GREETING_MESSAGE)))

    actual.lastResponse shouldEqual responseGenerator.DRUNK_MESSAGE
    actual.name shouldEqual userName
    actual.likeDrink shouldEqual userLikeDrink
  }
}
