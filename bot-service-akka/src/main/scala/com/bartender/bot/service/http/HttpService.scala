package com.bartender.bot.service.http

import akka.http.scaladsl.server.Directives._
import com.bartender.bot.service.api.ai.{ApiAiClientHttp, ApiAiResponseGenerator}
import com.bartender.bot.service.common.Config
import com.bartender.bot.service.fb.{FbMessageSender, FbMessengerSendApiClientHttp, FbMessengerService}
import com.bartender.bot.service.google.{GoogleBarResearcher, GooglePlacesClientHttp}
import com.bartender.bot.service.services._
import com.bartender.bot.service.thecocktaildb.{ThecocktaildbClientHttp, ThecocktaildbCocktailResearcher}


object HttpService extends Config {
  // TODO: use dependency injection container here
  val senderFbClient = new FbMessengerSendApiClientHttp()
  val sender = new FbMessageSender(senderFbClient)
  val dao = new MemoryDao()

  val apiAiClient = new ApiAiClientHttp()
  val responseGenerator = new ApiAiResponseGenerator(apiAiClient)
  val thecocktaildbClient = new ThecocktaildbClientHttp()
  val googlePlacesApiClient = new GooglePlacesClientHttp()
  val barDao = new MemoryBarDao()
  val barResearcher = new GoogleBarResearcher(googlePlacesApiClient, barDao)
  val cocktailDao = new CocktailDaoMemory()
  val cocktailResearcher = new ThecocktaildbCocktailResearcher(thecocktaildbClient, cocktailDao)
  val receiver = new MessageReceiverImpl(sender, responseGenerator, barResearcher, cocktailResearcher)
  val fbMessengerService = new FbMessengerService(receiver)

  val route = pathPrefix(apiVersion) {
    path(infoRoute) {
      complete(s"Hello to bartender bot api($apiVersion) :)")
    } ~
      fbMessengerService.route
  }
}
