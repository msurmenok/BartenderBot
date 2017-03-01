package com.bartender.bot.service.domain


case class Bar(id: String,
               name: String,
               address: Option[String],
               photoUrl: Option[String],
               location: Location)

case class BarDetails(website: Option[String],
                      rating: Option[Double],
                      priceLevel: Option[Int],
                      phoneNumber: Option[String],
                      extraPhotoUrl: Option[String],
                      reviews: Seq[BarReview]) {
  def priceLevelToStr(): String = {
    val str = (0 to priceLevel.getOrElse(0)).map(_ => "$").mkString("")
    if (priceLevel.isEmpty) {
      "-"
    }
    else {
      str
    }
  }
}

case class BarReview(author: String, text: String)

