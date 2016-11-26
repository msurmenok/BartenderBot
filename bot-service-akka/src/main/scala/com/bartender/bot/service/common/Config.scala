package com.bartender.bot.service.common

import com.typesafe.config.ConfigFactory

trait Config {
  private val config = ConfigFactory.load()
  private val httpConfig = config.getConfig("http")
  private val apiConfig = config.getConfig("api")
  private val routes = apiConfig.getConfig("routes")

  val httpHost = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("port")

  val apiVersion = apiConfig.getString("api_version")
  val fbMessengerVerifyToken = apiConfig.getString("fb_messenger_verify_token")

  val fbMessengerWebhook = routes.getString("fb-messenger-webhook")
  val infoRoute = routes.getString("info")

  val fbSendApiUrl = config.getConfig("fb_send_api").getString("url")

  val googlePlacesApiConf = config.getConfig("google_places_api")
}
