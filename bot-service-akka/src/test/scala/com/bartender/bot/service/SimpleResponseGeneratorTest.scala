package com.bartender.bot.service

import com.bartender.bot.service.domain.{Message, Recipient, RecipientInfo}
import org.scalatest.{FlatSpec, Matchers}
import com.bartender.bot.service.services.{MemoryDao, SimpleResponseGenerator}

class SimpleResponseGeneratorTest extends FlatSpec with Matchers {
  val dao = new MemoryDao()
  val responseGenerator = new SimpleResponseGenerator(dao)

  "random first user message" should "return greeting message" in {
    val recipient = Recipient("test1")
    val actual = responseGenerator.generateResponse(Message("random"), recipient)

    actual.message shouldEqual responseGenerator.GREETING_MESSAGE
    actual.action shouldEqual None
  }

  "first HI user message" should "return greeting message" in {
    val recipient = Recipient("test2")

    val actual = responseGenerator.generateResponse(Message("HI"), recipient)

    actual.message shouldEqual responseGenerator.GREETING_MESSAGE
    actual.action shouldEqual None
  }

  "Not first GOOD EVENING user message" should "return greeting message" in {
    val recipient = Recipient("test3")

    val userName = Some("Ivan")
    val userLikeDrink = Some(true)

    dao.saveOrUpdateRecipientInfo(recipient, RecipientInfo(name = userName, likeDrink = userLikeDrink,
      lastResponse = Message("random")))
    val actual = responseGenerator.generateResponse(Message("GOOD EVENING"), recipient)

    actual.message shouldEqual responseGenerator.GREETING_MESSAGE
    actual.action shouldEqual None
  }

  "When bot don't know about like user drink or not, bot" should "ask it" in {
    val recipient = Recipient("test4")
    dao.saveOrUpdateRecipientInfo(recipient, RecipientInfo(lastResponse = Message("random")))

    val actual = responseGenerator.generateResponse(Message("random"), recipient)

    actual.message shouldEqual responseGenerator.WANT_DRINK_QUESTION
    actual.action shouldEqual None
  }

  "When bot don't know user name, but know about like user drink or not, bot" should "ask user name" in {
    val userLikeDrink = Some(true)
    val recipient = Recipient("test5")
    dao.saveOrUpdateRecipientInfo(recipient, RecipientInfo(likeDrink = userLikeDrink, lastResponse = Message("random")))

    val actual = responseGenerator.generateResponse(Message("random"), recipient)

    actual.message shouldEqual responseGenerator.NAME_QUESTION
    actual.action shouldEqual None
  }

  "When user say his name, bot" should "return meeting message" in {

    val recipientMessage = Message("name")
    val recipient = Recipient("test6")
    dao.saveOrUpdateRecipientInfo(recipient, RecipientInfo(lastResponse = responseGenerator.NAME_QUESTION))


    val actual = responseGenerator.generateResponse(recipientMessage, recipient)

    actual.message.text.contains(recipientMessage.text) shouldEqual true
    actual.action shouldEqual None
  }

  "When user say about like he drink or no, bot" should "identifications it" in {
    val recipientMessage = Message("yes")
    val recipient = Recipient("test7")
    dao.saveOrUpdateRecipientInfo(recipient, RecipientInfo(lastResponse = responseGenerator.WANT_DRINK_QUESTION))

    val actual = responseGenerator.generateResponse(recipientMessage, recipient)

    actual.message shouldEqual responseGenerator.NAME_QUESTION
    actual.action shouldEqual None
  }

  "When bot know user name and about like user drink and user not send HELLO" should "return info message" in {

    val userName = Some("Ivan")
    val userLikeDrink = Some(true)
    val userNotLikeDrink = Some(false)


    val recipient1 = Recipient("test8")
    dao.saveOrUpdateRecipientInfo(recipient1, RecipientInfo(name = userName, likeDrink = userLikeDrink,
      lastResponse = Message("random")))

    val recipient2 = Recipient("test9")
    dao.saveOrUpdateRecipientInfo(recipient2, RecipientInfo(name = userName, likeDrink = userNotLikeDrink,
      lastResponse = Message("random")))

    val actual1 = responseGenerator.generateResponse(Message("random"), recipient1)

    val actual2 = responseGenerator.generateResponse(Message("random"), recipient2)

    actual1.message shouldEqual responseGenerator.INFO_MESSAGE
    actual1.action shouldEqual None

    actual2.message shouldEqual responseGenerator.IF_YOU_NOT_DRINK_INFO_MESSAGE
    actual2.action shouldEqual None
  }

  "When bot know user name and about like user drink and user not send HELLO" +
    " and last response is info message" should "return echо message" in {

    val randomMessage = Message("random")
    val userName = Some("Ivan")
    val userLikeDrink = Some(true)


    val recipient1 = Recipient("test10")
    dao.saveOrUpdateRecipientInfo(recipient1, RecipientInfo(name = userName, likeDrink = userLikeDrink,
      lastResponse = responseGenerator.INFO_MESSAGE))

    val recipient2 = Recipient("test11")
    dao.saveOrUpdateRecipientInfo(recipient2, RecipientInfo(name = userName, likeDrink = userLikeDrink,
      lastResponse = responseGenerator.IF_YOU_NOT_DRINK_INFO_MESSAGE))

    val actual1 = responseGenerator.generateResponse(randomMessage, recipient1)

    val actual2 = responseGenerator.generateResponse(randomMessage, recipient2)


    actual1.message.text.contains(randomMessage.text) shouldEqual true
    actual1.action shouldEqual None

    actual2.message.text.contains(randomMessage.text) shouldEqual true
    actual2.action shouldEqual None
  }

  "When user send HELLO second time" should "return DRUNK_MESSAGE" in {
    val userName = Some("Ivan")
    val userLikeDrink = Some(true)

    val recipient = Recipient("test12")
    dao.saveOrUpdateRecipientInfo(recipient, RecipientInfo(name = userName, likeDrink = userLikeDrink,
      lastResponse = responseGenerator.GREETING_MESSAGE))

    val actual = responseGenerator.generateResponse(Message("Hello"), recipient)

    actual.message shouldEqual responseGenerator.DRUNK_MESSAGE
    actual.action shouldEqual None
  }
}
