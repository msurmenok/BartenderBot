package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Cocktail, CocktailReceipt}

trait CocktailResearcher {

  def cocktailReceipt(id: Option[String]): Option[(Cocktail, CocktailReceipt)]

  def cocktailByAlcohol(alcohol: String): Seq[Cocktail]

}

