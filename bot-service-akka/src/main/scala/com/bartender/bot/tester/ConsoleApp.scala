package com.bartender.bot.tester

import com.bartender.bot.service.api.ai.{ApiAiClient, ApiAiClientHttp, ApiAiResponseGenerator}
import com.bartender.bot.service.domain.{Bar, Location, Message, Recipient}
import com.bartender.bot.service.google.{GoogleBarResearcher, GooglePlacesClientHttp}
import com.bartender.bot.service.services._
import com.bartender.bot.service.thecocktaildb.{ThecocktaildbClientHttp, ThecocktaildbCocktailResearcher}

object ConsoleApp {
  val patternLocation = "l(\\d{1,3}\\.\\d+),(\\d{1,3}\\.\\d+)"

  def main(args: Array[String]): Unit = {
    val sender = new ConsoleSender()
    val dao = new MemoryDao()

    val googlePlacesApiClient = new GooglePlacesClientHttp()
    val barDao = new MemoryBarDao()
    val barResearcher = new GoogleBarResearcher(googlePlacesApiClient, barDao)
    val apiAiClient = new ApiAiClientHttp()
    val responseGenerator = new ApiAiResponseGenerator(apiAiClient)
    val thecocktaildbClient = new ThecocktaildbClientHttp()
    val cocktailResearcher = new ThecocktaildbCocktailResearcher(thecocktaildbClient)
    val receiver = new MessageReceiverImpl(sender, responseGenerator, barResearcher, cocktailResearcher)
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
