package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Bar, Location}
import com.bartender.bot.service.google.{GPType, GooglePlacesClient}

trait BarResearcher {
  def findNearestBars(location: Location): Seq[Bar]
}

class GoogleBarResearcher(val googlePlacesClient: GooglePlacesClient = new GooglePlacesClient())
  extends BarResearcher {

  def findNearestBars(location: Location): Seq[Bar] = {

    googlePlacesClient.nearbySearch(location = s"${location.lat},${location.lng}", types = GPType.bar.toString)
      .map(_.results).getOrElse(Seq.empty)
      .filter(_.opening_hours.exists(_.open_now))
      .map(place => Bar(place.name, googlePlacesClient.getPhotoUrl(place.photos.getOrElse(Seq.empty).headOption)))
  }
}