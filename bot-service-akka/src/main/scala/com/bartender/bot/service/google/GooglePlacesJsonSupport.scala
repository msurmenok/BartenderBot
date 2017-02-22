package com.bartender.bot.service.google

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}


trait GooglePlacesJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object GPTypeFormat extends RootJsonFormat[GPType.Value] {
    //todo: maybe possible create common method for parse enum
    def write(obj: GPType.Value): JsValue = JsString(obj.toString)

    def read(json: JsValue): GPType.Value = json match {
      case JsString(str) => GPType.withName(str)
      case _ => throw DeserializationException("Enum string expected")
    }
  }

  implicit val gpaltIds = jsonFormat2(GPAltIds)
  implicit val gpLocation = jsonFormat2(GPLocation)
  implicit val gpGeometry = jsonFormat1(GPGeometry)
  implicit val gpOpeningHours = jsonFormat1(GPOpeningHours)
  implicit val gpPhotos = jsonFormat3(GPPhotos)
  implicit val gpAddressComponents = jsonFormat3(GPAddressComponents)
  implicit val gpAspects = jsonFormat2(GPAspects)
  implicit val gpReview = jsonFormat7(GPReview)
  implicit val gpResult = jsonFormat14(GPResult)
  implicit val gpDetailResult = jsonFormat21(GPDetailResult)
  implicit val gpGPDetailResponse = jsonFormat3(GPDetailResponse)
  implicit val gpResponse = jsonFormat3(GPResponse)
}
