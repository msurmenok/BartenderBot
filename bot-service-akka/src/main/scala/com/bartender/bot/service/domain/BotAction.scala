package com.bartender.bot.service.domain

sealed trait BotAction

object BotAction {

  case class NearestBar() extends BotAction

  case class InputWelcome() extends BotAction

  case class CocktailByAlcohol(alcoholType: String) extends BotAction

  case class CocktailReceiptRandom() extends BotAction

}
