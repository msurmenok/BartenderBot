package com.bartender.bot.service.thecocktaildb

import com.bartender.bot.service.domain.{Cocktail, CocktailIngredient, CocktailReceipt}
import com.bartender.bot.service.services.{CocktailDao, CocktailResearcher}
import com.bartender.bot.service.thecocktaildb.ThecocktaildbModel.ThecocktaildbCommonDrinkInfo


class ThecocktaildbCocktailResearcher(client: ThecocktaildbClient, dao: CocktailDao) extends CocktailResearcher {

  def cocktailReceipt(id: Option[String]): Option[(Cocktail, CocktailReceipt)] = {
    id match {
      case Some(_id) => dao.cocktailDetails(_id) match {
        case None =>
          client.cocktailDetails(_id).map(mapCocktailReceipt) map {
            cocktailReceipt =>
              dao.saveCocktailReceipt(_id, cocktailReceipt)
              cocktailReceipt
          }
        case Some(cocktailReceipt) => Some(cocktailReceipt)
      }
      case None => client.randomCocktail().map(mapCocktailReceipt)
    }
  }

  def cocktailByAlcohol(alcohol: String): Seq[Cocktail] = {
    dao.cocktailsByAlcohol(alcohol).getOrElse {
      val cocktailsByAlcohol = client.cocktailsByAlcohol(alcohol).map(mapCocktail)
      dao.saveCocktailsByAlcohol(alcohol, cocktailsByAlcohol)
      cocktailsByAlcohol
    }

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
