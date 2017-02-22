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
                      reviews: Seq[String])

