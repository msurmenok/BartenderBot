package com.bartender.bot.tester

import com.bartender.bot.service.domain.{Location, Message, Recipient}
import com.bartender.bot.service.services.{GoogleBarResearcher, MemoryDao, MessageReceiverImpl, SimpleResponseGenerator}

object ConsoleApp {
  val patternLocation = "l(\\d{1,3}\\.\\d+),(\\d{1,3}\\.\\d+)"

  def main(args: Array[String]): Unit = {
    val sender = new ConsoleSender()
    val dao = new MemoryDao()
    val barResearcher = new GoogleBarResearcher()
    val responseGenerator = new SimpleResponseGenerator()
    val receiver = new MessageReceiverImpl(sender, dao, responseGenerator, barResearcher)
    val recipient = Recipient("test_recipient")

    println("Welcome!")
    println("For searching nearest bars input coordinate as \"l{latitude},{longitude}\"")

    while(true) {
      val input = scala.io.StdIn.readLine()
      if(input matches patternLocation){
        val coordinates = input.substring(1).split(",")
        receiver.receiveNearestBars(Location(lat = coordinates(0).toDouble, lng = coordinates(1).toDouble), recipient)
      }else{
        val message = Message(input)
        receiver.Receive(message, recipient)
      }
    }
  }
}
