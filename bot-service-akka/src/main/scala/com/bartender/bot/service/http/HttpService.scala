package com.bartender.bot.service.http

import akka.http.scaladsl.server.Directives._
import com.bartender.bot.service.common.Config
import com.bartender.bot.service.fb.FbMessengerService


object HttpService extends Config {
  val route = pathPrefix(apiVersion) {
    path(infoRoute) {
      complete(s"Hello to bartender bot api($apiVersion) :)")
    } ~
      FbMessengerService.route
  }

}
