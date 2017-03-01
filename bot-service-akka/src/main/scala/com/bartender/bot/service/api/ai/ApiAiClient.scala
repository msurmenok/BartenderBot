package com.bartender.bot.service.api.ai

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.bartender.bot.service.api.ai.ApiAiModel.{ApiAiRequest, ApiAiResponse}
import com.bartender.bot.service.common.{Config, Logging}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

trait ApiAiClient {
  def generateAnswer(query: String, sessionId: String): Option[ApiAiResponse]
}

class ApiAiClientHttp extends ApiAiClient with ApiAiJsonSupport with Config with Logging {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val baseUrl = apiApiConf.getString("url")
  val clientToken = apiApiConf.getString("client_token")

  def generateAnswer(query: String, sessionId: String): Option[ApiAiResponse] = {

    rootLogger.info(s"process natural language for: $query")

    val body = ApiAiRequest(query = Seq(query), sessionId = sessionId)

    val httpRequest = HttpRequest(
      method = HttpMethods.POST,
      uri = s"${baseUrl}query?v=20150910",
      entity = HttpEntity(ContentTypes.`application/json`,
        apiAiRequestFormat.write(body).compactPrint),
      headers = List(Authorization(OAuth2BearerToken(clientToken)))
    )

    val httpResponse = Await.result(Http().singleRequest(httpRequest), Duration.Inf)

    rootLogger.info(s"response status: ${httpResponse.status}")

    if (httpResponse.status.isSuccess()) {
      Some(Await.result(Unmarshal(httpResponse.entity).to[ApiAiResponse], Duration.Inf))
    } else {
      None
    }
  }
}
