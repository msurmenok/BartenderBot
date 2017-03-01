package com.bartender.bot.service.thecocktaildb

import com.bartender.bot.service.domain.{Cocktail, CocktailIngredient, CocktailReceipt}
import com.bartender.bot.service.services.CocktailResearcher
import com.bartender.bot.service.thecocktaildb.ThecocktaildbModel.ThecocktaildbCommonDrinkInfo


class ThecocktaildbCocktailResearcher(client: ThecocktaildbClient) extends CocktailResearcher {

  def cocktailReceipt(id: Option[String]): Option[(Cocktail, CocktailReceipt)] = {
    id match {
      case Some(_id) => client.cocktailDetails(_id).map(mapCocktailReceipt)
      case None => client.randomCocktail().map(mapCocktailReceipt)
    }
  }

  def cocktailByAlcohol(alcohol: String): Seq[Cocktail] = {
    client.cocktailsByAlcohol(alcohol).map(mapCocktail)
  }

  private def mapCocktailReceipt: (ThecocktaildbModel.ThecocktaildbDrink) => (Cocktail, CocktailReceipt) = {
    drink =>
      val cocktailReceipt = CocktailReceipt(
        drink.commonInfo.strInstructions,
        drink.commonInfo.strGlass,
        drink.ingredients.map(ingr => CocktailIngredient(ingr.name, ingr.measure)))

      (mapCocktail(drink.commonInfo), cocktailReceipt)
  }

  private def mapCocktail(drink: ThecocktaildbCommonDrinkInfo) = Cocktail(drink.idDrink, drink.strDrink, drink.strDrinkThumb)

}
