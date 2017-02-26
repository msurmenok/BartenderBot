package com.bartender.bot.tester

import com.bartender.bot.service.api.ai.ApiAiClient
import com.bartender.bot.service.domain.{Bar, Location, Message, Recipient}
import com.bartender.bot.service.services._

object ConsoleApp {
  val patternLocation = "l(\\d{1,3}\\.\\d+),(\\d{1,3}\\.\\d+)"

  def main(args: Array[String]): Unit = {
    val sender = new ConsoleSender()
    val dao = new MemoryDao()
    val barResearcher = new GoogleBarResearcher()
    val responseGenerator = new ApiAiResponseGenerator(new ApiAiClient())
    val receiver = new MessageReceiverImpl(sender, responseGenerator, barResearcher)
    val recipient = Recipient("test_recipient")

    println("Welcome!")
    println("For searching nearest bars input coordinate as \"l{latitude},{longitude}\"")

    var coordinates: Option[Location] = None
    var lastBar: Option[Bar] = None
    var offset = 0
    while (true) {
      val input = scala.io.StdIn.readLine()
      if (input matches patternLocation) {
        val args = input.substring(1).split(",")
        coordinates = Some(Location(lat = args(0).toDouble, lng = args(1).toDouble))
        lastBar = receiver.receiveNearestBar(coordinates.get, recipient, offset)
      } else if (coordinates.isDefined && input.equals("next")) {
        offset += 1
        lastBar = receiver.receiveNearestBar(coordinates.get, recipient, offset)
      } else if (lastBar.isDefined && input.equals("details")) {
        receiver.receiveBarDetails(lastBar.get.id, recipient)
        lastBar = None
      } else {
        coordinates = None
        lastBar = None
        offset = 0
        val message = Message(input)
        receiver.receive(message, recipient)
      }
    }
  }
}
