package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Message, Recipient, RecipientInfo}

import scala.collection.mutable

trait Dao {
  def saveOrUpdateRecipientInfo(recipient: Recipient, recipientInfo: RecipientInfo): Unit

  def getRecipientInfo(recipient: Recipient): Option[RecipientInfo]

  def saveRecipientMessage(message: Message): Unit

  def saveResponse(response: Message): Unit
}

class MemoryDao() extends Dao {

  private val recipientsInfo = mutable.HashMap[Recipient, RecipientInfo]()

  def saveOrUpdateRecipientInfo(recipient: Recipient, recipientInfo: RecipientInfo) {
    recipientsInfo(recipient) = recipientInfo
  }

  def getRecipientInfo(recipient: Recipient): Option[RecipientInfo] = {
    recipientsInfo.get(recipient)
  }

  def saveRecipientMessage(message: Message): Unit = {
    // TODO:
  }

  override def saveResponse(response: Message): Unit = {
    // TODO:
  }
}
