package com.bartender.bot.service.services
import com.bartender.bot.service.domain.{Message, Recipient}

class MessageReceiverImpl(sender: MessageSender) extends MessageReceiver {
  override def Receive(message: Message, recipient: Recipient): Unit =
  {
    this.SaveMessage(message)

    val response = this.GenerateResponse(message)
    this.SaveMessage(response)

    sender.SendMessage(response, recipient)
  }

  private def GenerateResponse(message: Message): Message = {
    Message(message.text)
  }

  private def SaveMessage(message: Message): Unit =
  {
    // TODO: store the message in DB
  }
}
