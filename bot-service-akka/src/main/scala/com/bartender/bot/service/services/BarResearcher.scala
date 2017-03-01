package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Bar, BarDetails, Location}

trait BarResearcher {
  def getBarDetails(barId: String): Option[BarDetails]

  def findNearestBars(location: Location): Seq[Bar]
}

