package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Bar, BarDetails, Location}

import scala.collection.mutable

trait BarDao {

  def getBarDetails(barId: String): Option[BarDetails]

  def getBarsByLocation(location: Location): Option[Seq[Bar]]

  def saveBarsByLocation(location: Location, bars: Seq[Bar])

  def saveBarDetails(barId: String, barDetails: BarDetails)

}


class MemoryBarDao() extends BarDao {

  val bar_by_location_time_mills_period_available = 60 * 60 * 1000

  val locationDateUpdate = mutable.HashMap[Location, Long]()
  val barsByLocation = mutable.HashMap[Location, Seq[Bar]]()
  val barDetails = mutable.HashMap[String, BarDetails]()

  def getBarDetails(barId: String): Option[BarDetails] = {
    barDetails.get(barId)
  }

  def getBarsByLocation(location: Location): Option[Seq[Bar]] = {
    val expired = locationDateUpdate.get(location)
      .forall(millis => System.currentTimeMillis() - millis > bar_by_location_time_mills_period_available)
    if (expired) {
      locationDateUpdate.remove(location)
      barsByLocation.remove(location)
      None
    } else {
      barsByLocation.get(location)
    }
  }

  def saveBarsByLocation(location: Location, bars: Seq[Bar]): Unit = {
    locationDateUpdate += location -> System.currentTimeMillis()
    barsByLocation += location -> bars
  }

  def saveBarDetails(barId: String, details: BarDetails) {
    barDetails += barId -> details
  }
}
