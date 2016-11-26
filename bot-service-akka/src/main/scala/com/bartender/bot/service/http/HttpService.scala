package com.bartender.bot.service.http

import akka.http.scaladsl.server.Directives._
import com.bartender.bot.service.common.Config
import com.bartender.bot.service.fb.{FbMessageSender, FbMessengerSendApiClient, FbMessengerService}
import com.bartender.bot.service.services.{GoogleBarResearcher, MemoryDao, MessageReceiverImpl, SimpleResponseGenerator}


object HttpService extends Config {
  // TODO: use dependency injection container here
  val sender = new FbMessageSender()
  val dao = new MemoryDao()
  val responseGenerator = new SimpleResponseGenerator()
  val barResearcher = new GoogleBarResearcher()
  val receiver = new MessageReceiverImpl(sender, dao, responseGenerator, barResearcher)
  val fbMessengerService = new FbMessengerService(receiver)

  val route = pathPrefix(apiVersion) {
    path(infoRoute) {
      complete(s"Hello to bartender bot api($apiVersion) :)")
    } ~
      fbMessengerService.route
  }
}
