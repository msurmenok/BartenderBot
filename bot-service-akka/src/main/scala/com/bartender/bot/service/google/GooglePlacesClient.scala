package com.bartender.bot.service.google

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.bartender.bot.service.common.{Config, Logging}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

trait GooglePlacesClient {

  def nearbySearch(location: String, types: String, radius: String = "1000"): Option[GPResponse]

  def details(placeId: String): Option[GPDetailResponse]

  def getPhotoUrl(headOption: Option[GPPhotos]): Option[String]
}

class GooglePlacesClientHttp extends GooglePlacesClient with GooglePlacesJsonSupport with Config with Logging {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val baseUrl = googlePlacesApiConf.getString("base_url")
  val placeDetailsUrl = baseUrl + googlePlacesApiConf.getString("place_detail_path")
  val nearbySearchUrl = baseUrl + googlePlacesApiConf.getString("nearby_search_path")
  val photoUrl = baseUrl + googlePlacesApiConf.getString("photo_path")
  val apiKey = googlePlacesApiConf.getString("api_key")

  def nearbySearch(location: String, types: String, radius: String): Option[GPResponse] = {

    rootLogger.info(s"find bars nearest location($location)")

    val httpRequest = HttpRequest(method = HttpMethods.GET,
      uri = s"$nearbySearchUrl?location=$location&radius=$radius&types=$types&key=$apiKey")
    val fResponse = Http().singleRequest(httpRequest)
    val response = Await.result(fResponse, Duration.Inf)

    rootLogger.info(s"response status: ${response.status}")

    if (response.status.isSuccess()) {
      Some(Await.result(Unmarshal(response.entity).to[GPResponse], Duration.Inf))
    } else {
      None
    }
  }

  def details(placeId: String): Option[GPDetailResponse] = {
    rootLogger.info(s"request place details ($placeId)")

    val httpRequest = HttpRequest(method = HttpMethods.GET,
      uri = s"$placeDetailsUrl?placeid=$placeId&key=$apiKey")
    val fResponse = Http().singleRequest(httpRequest)
    val response = Await.result(fResponse, Duration.Inf)

    rootLogger.info(s"response status: ${response.status}")

    if (response.status.isSuccess()) {
      val result = Some(Await.result(Unmarshal(response.entity).to[GPDetailResponse], Duration.Inf))
      result
    } else {
      None
    }

  }

  def getPhotoUrl(headOption: Option[GPPhotos]): Option[String] = {
    headOption.map(gpPhoto =>
      s"$photoUrl?maxwidth=400&photoreference=${gpPhoto.photo_reference}&key=$apiKey")
  }
}
