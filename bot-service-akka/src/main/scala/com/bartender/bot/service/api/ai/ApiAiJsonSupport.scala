package com.bartender.bot.service.api.ai

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bartender.bot.service.api.ai.ApiAiModel._
import spray.json.DefaultJsonProtocol

trait ApiAiJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val apiAiMetadataFormat = jsonFormat4(ApiAiMetadata)
  implicit val apiAiMessageFormat = jsonFormat2(ApiAiMessage)
  implicit val apiAiFulfillmentFormat = jsonFormat2(ApiAiFulfillment)
  implicit val apiApiContextFormat = jsonFormat3(ApiApiContext)
  implicit val apiAiResultFormat = jsonFormat9(ApiAiResult)
  implicit val apiAiStatusFormat = jsonFormat2(ApiAiStatus)
  implicit val apiAiResponseFormat = jsonFormat6(ApiAiResponse)
  implicit val apiAiLocationFormat = jsonFormat2(ApiAiLocation)
  implicit val apiAiRequestFormat = jsonFormat6(ApiAiRequest)

}
