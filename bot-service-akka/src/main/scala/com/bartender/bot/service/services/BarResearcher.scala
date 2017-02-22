package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Bar, BarDetails, Location}
import com.bartender.bot.service.google.{GPType, GooglePlacesClient}

import scala.collection.mutable

trait BarResearcher {
  def getBarDetails(barId: String): Option[BarDetails]

  def findNearestBars(location: Location): Seq[Bar]
}

class GoogleBarResearcher(val googlePlacesClient: GooglePlacesClient = new GooglePlacesClient())
  extends BarResearcher {
  val locationDateUpdate = mutable.HashMap[Location, Long]()
  val barsByLocation = mutable.HashMap[Location, Seq[Bar]]()
  val barDetails = mutable.HashMap[String, BarDetails]()

  def findNearestBars(location: Location): Seq[Bar] = {
    val expired = locationDateUpdate.get(location).forall(milles => System.currentTimeMillis() - milles > 60 * 60 * 1000)
    if (expired)
      fromApi(location)
    else
      barsByLocation.getOrElse(location, fromApi(location))

  }

  private def fromApi(location: Location): Seq[Bar] = {
    val bars = googlePlacesClient.nearbySearch(location = s"${location.lat},${location.lng}", types = GPType.bar.toString)
      .map(_.results).getOrElse(Seq.empty)
      .filter(_.opening_hours.exists(_.open_now))
      .sortBy(_.rating)(Ordering[Option[Double]].reverse)
      .map(place =>
        Bar(
          place.place_id,
          place.name,
          place.vicinity,
          googlePlacesClient.getPhotoUrl(place.photos.getOrElse(Seq.empty).headOption),
          Location(place.geometry.location.lat, place.geometry.location.lng)))
    locationDateUpdate += location -> System.currentTimeMillis()
    barsByLocation += location -> bars
    bars
  }

  def getBarDetails(barId: String): Option[BarDetails] = {
    if (barDetails.contains(barId)) {
      barDetails.get(barId)
    } else {
      fromApi(barId)
    }
  }

  private def fromApi(barId: String): Option[BarDetails] = {
    googlePlacesClient.details(barId)
      .map(_.result)
      .getOrElse(None)
      .map { placeDetail =>
        val details = BarDetails(
          placeDetail.website,
          placeDetail.rating,
          placeDetail.price_level,
          placeDetail.international_phone_number,
          placeDetail.reviews.map(_.map(_.text)).getOrElse(Seq.empty))
        barDetails += barId -> details
        details
      }
  }
}