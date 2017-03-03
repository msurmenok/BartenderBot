package com.bartender.bot.service.services

import com.bartender.bot.service.domain.{Cocktail, CocktailReceipt}

import scala.collection.mutable


trait CocktailDao {
  def saveCocktailsByAlcohol(alcohol: String, cocktails: Seq[Cocktail])

  def cocktailsByAlcohol(alcohol: String): Option[Seq[Cocktail]]

  def saveCocktailReceipt(id: String, cocktailReceipt: (Cocktail, CocktailReceipt))

  def cocktailDetails(id: String): Option[(Cocktail, CocktailReceipt)]
}

class CocktailDaoMemory extends CocktailDao {

  private val cocktailReceipts = mutable.HashMap[String, (Cocktail, CocktailReceipt)]()

  private val cocktailsByAlcohol = mutable.HashMap[String, Seq[Cocktail]]()

  def saveCocktailsByAlcohol(alcohol: String, cocktails: Seq[Cocktail]): Unit = {
    cocktailsByAlcohol += alcohol -> cocktails
  }

  def cocktailsByAlcohol(alcohol: String): Option[Seq[Cocktail]] = {
    cocktailsByAlcohol.get(alcohol)
  }

  def saveCocktailReceipt(id: String, cocktailReceipt: (Cocktail, CocktailReceipt)): Unit = {
    cocktailReceipts += id -> cocktailReceipt
  }

  def cocktailDetails(id: String): Option[(Cocktail, CocktailReceipt)] = {
    cocktailReceipts.get(id)
  }
}
