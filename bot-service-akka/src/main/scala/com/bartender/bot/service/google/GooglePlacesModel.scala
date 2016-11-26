package com.bartender.bot.service.google


object GPType extends Enumeration {
  type EnumA = Value
  val bar, liquor_store, restaurant = Value
}

case class GPLocation(
                       lat: Double,
                       lng: Double
                     )

case class GPGeometry(
                       location: GPLocation
                     )

case class GPOpeningHours(
                           open_now: Boolean
                         )

case class GPPhotos(
                     height: Double,
                     photo_reference: String,
                     width: Double
                   )

case class GPAltIds(
                     place_id: String,
                     scope: String
                   )

case class GPResult(
                     geometry: GPGeometry,
                     icon: String,
                     id: String,
                     name: String,
                     opening_hours: Option[GPOpeningHours],
                     photos: Option[Seq[GPPhotos]],
                     place_id: String,
                     scope: Option[String],
                     alt_ids: Option[Seq[GPAltIds]],
                     price_level: Option[Int],
                     rating: Option[Double],
                     reference: String,
                     types: Seq[String],
                     vicinity: Option[String]
                   )

case class GPResponse(results: Seq[GPResult], status: String, html_attributions: Seq[String])


