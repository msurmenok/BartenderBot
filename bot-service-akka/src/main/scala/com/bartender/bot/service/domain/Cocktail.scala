package com.bartender.bot.service.domain

case class Cocktail(id: String, name: String, imageUrl: Option[String])

case class CocktailReceipt(instruction: Option[String], glass: Option[String], ingredients: Seq[CocktailIngredient]) {
  def isEmpty: Boolean = {
    instruction.isEmpty && ingredients.isEmpty
  }
}

case class CocktailIngredient(name: String, measure: String)
