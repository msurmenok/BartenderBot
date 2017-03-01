package com.bartender.bot.service.thecocktaildb

object ThecocktaildbModel {

  case class ThecocktaildbCommonDrinkInfo(
                                           idDrink: String,
                                           strDrink: String,
                                           strCategory: Option[String],
                                           strAlcoholic: Option[String],
                                           strGlass: Option[String],
                                           strInstructions: Option[String],
                                           strDrinkThumb: Option[String],
                                           dateModified: Option[String]
                                         )

  case class ThecocktaildbIngredient(name: String, measure: String)

  case class ThecocktaildbDrink(commonInfo: ThecocktaildbCommonDrinkInfo, ingredients: Seq[ThecocktaildbIngredient])

  case class ThecocktaildbResponse(drinks: Option[Seq[ThecocktaildbDrink]])

}
