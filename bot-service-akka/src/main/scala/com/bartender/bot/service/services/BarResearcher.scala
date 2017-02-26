package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Bar, BarDetails, BarReview, Location}
import com.bartender.bot.service.google.{GPType, GooglePlacesClient}

trait BarResearcher {
  def getBarDetails(barId: String): Option[BarDetails]

  def findNearestBars(location: Location): Seq[Bar]
}

class GoogleBarResearcher(val googlePlacesClient: GooglePlacesClient = new GooglePlacesClient(),
                          val barDao: BarDao = new MemoryBarDao())
  extends BarResearcher {

  def findNearestBars(location: Location): Seq[Bar] = {
    barDao.getBarsByLocation(location).getOrElse {
      val nearestBars = fromApi(location)
      barDao.saveBarsByLocation(location, nearestBars)
      nearestBars
    }
  }

  private def fromApi(location: Location): Seq[Bar] = {
    googlePlacesClient.nearbySearch(location = s"${location.lat},${location.lng}", types = GPType.bar.toString)
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
  }

  def getBarDetails(barId: String): Option[BarDetails] = {
    barDao.getBarDetails(barId) match {
      case None => fromApi(barId).map { details =>
        barDao.saveBarDetails(barId, details)
        details
      }
      case _ => _
    }
  }

  private def fromApi(barId: String): Option[BarDetails] = {
    googlePlacesClient.details(barId)
      .map(_.result)
      .getOrElse(None)
      .map { placeDetail =>
        BarDetails(
          placeDetail.website,
          placeDetail.rating,
          placeDetail.price_level,
          placeDetail.international_phone_number,
          googlePlacesClient.getPhotoUrl(placeDetail.photos.getOrElse(Seq.empty).lastOption),
          placeDetail.reviews.map(_.map(review => BarReview(review.author_name, review.text))).getOrElse(Seq.empty))
      }
  }
}