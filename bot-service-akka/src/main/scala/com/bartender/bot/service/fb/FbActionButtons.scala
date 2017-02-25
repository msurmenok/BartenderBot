package com.bartender.bot.service.fb

import com.bartender.bot.service.domain.Location


object FbActionButtons {
  val BAR_DETAILS = "Details"
  val SHOW_NEXT_BAR = "Show one more"

  private val ARG_DIVIDER = ";"

  def barDetails(barId: String): FbTemplateButton = {
    FbTemplateButton(FbTemplateButtonsType.postback, title = BAR_DETAILS,
      payload = Some(s"$BAR_DETAILS$ARG_DIVIDER$barId"))
  }

  def barShowNext(location: Location, offset: Int): FbTemplateButton = {
    FbTemplateButton(FbTemplateButtonsType.postback, title = SHOW_NEXT_BAR,
      payload = Some(barShowNextPayloadToStr(location, offset)))
  }

  def getBarDetailsPayload(string: String): String = {
    val arg = string.split(ARG_DIVIDER)
    arg(1)
  }

  def getBarShowNextPayload(string: String): (Location, Int) = {
    val arg = string.split(ARG_DIVIDER)
    val location = Location(arg(1).toDouble, arg(2).toDouble)
    val offset = arg(3).toInt
    (location, offset)
  }

  private def barShowNextPayloadToStr(location: Location, offset: Int): String = {
    s"$SHOW_NEXT_BAR$ARG_DIVIDER${location.lat}$ARG_DIVIDER${location.lng}$ARG_DIVIDER$offset"
  }
}
