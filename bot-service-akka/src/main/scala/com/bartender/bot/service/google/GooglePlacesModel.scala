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

case class GPAddressComponents(
                                long_name: String,
                                short_name: String,
                                types: List[String]
                              )

case class GPAspects(
                      rating: Double,
                      `type`: String
                    )

case class GPReview(
                     aspects: List[GPAspects],
                     author_name: String,
                     author_url: Option[String],
                     language: String,
                     rating: Double,
                     text: String,
                     time: Double
                   )

case class GPDetailResult(
                           address_components: Option[List[GPAddressComponents]],
                           formatted_address: Option[String],
                           formatted_phone_number: Option[String],
                           international_phone_number: Option[String],
                           reviews: Option[Seq[GPReview]],
                           url: String,
                           website: Option[String],
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

case class GPDetailResponse(result: Option[GPDetailResult], status: String, html_attributions: Seq[String])


