package com.bartender.bot.service.fb

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import com.bartender.bot.service.common.{Config, Logging}

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object FbMessengerSendApiClient extends JsonSupport with Config with Logging {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  def sendTextMessage(recipient: Recipient, text: String): Unit = {
    rootLogger.info(s"sending message to recipient(${recipient.id}): $text")
    val body = FbMessengerRequest(recipient, Some(SendMessage(Some(text))))
    val response = sendFbMessengerRequest(body)
    rootLogger.info(s"result messge sending: ${response.status}")
    rootLogger.info(s"${response.entity}")
  }

  private def sendFbMessengerRequest(body: FbMessengerRequest): HttpResponse = {
    val httpRequest = HttpRequest(method = HttpMethods.POST, uri = fbSendApiUrl,
      entity = HttpEntity(ContentTypes.`application/json`, fbMessengerRequestFormat.write(body).compactPrint))
    val fResponse = Http().singleRequest(httpRequest)
    Await.result(fResponse, Duration.Inf)
  }
}
