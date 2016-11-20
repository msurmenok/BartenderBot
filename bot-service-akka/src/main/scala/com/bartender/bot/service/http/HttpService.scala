package com.bartender.bot.service.http

import akka.http.scaladsl.server.Directives._
import com.bartender.bot.service.common.Config
import com.bartender.bot.service.fb.{FbMessageSender, FbMessengerService}
import com.bartender.bot.service.services.MessageReceiverImpl


object HttpService extends Config {
  // TODO: use dependency injection container here
  val sender = new FbMessageSender()
  val receiver = new MessageReceiverImpl(sender)
  val fbMessengerService = new FbMessengerService(receiver)

  val route = pathPrefix(apiVersion) {
    path(infoRoute) {
      complete(s"Hello to bartender bot api($apiVersion) :)")
    } ~
      fbMessengerService.route
  }
}
