package com.bartender.bot.tester

import com.bartender.bot.service.domain.{Message, Recipient}
import com.bartender.bot.service.services.{MemoryDao, MessageReceiverImpl, SimpleResponseGenerator}

object ConsoleApp {
  def main(args: Array[String]): Unit = {
    val sender = new ConsoleSender()
    val dao = new MemoryDao()
    val responseGenerator = new SimpleResponseGenerator()
    val receiver = new MessageReceiverImpl(sender, dao, responseGenerator)
    val recipient = new Recipient("test_recipient")

    println("Welcome!")

    while(true) {
      val input = scala.io.StdIn.readLine()
      val message = new Message(input)
      receiver.Receive(message, recipient)
    }
  }
}
