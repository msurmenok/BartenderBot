package com.bartender.bot.service.api.ai

import com.bartender.bot.service.api.ai.ApiAiModel.{ApiAiActions, ApiAiParameters}
import com.bartender.bot.service.domain._
import com.bartender.bot.service.services.ResponseGenerator

class ApiAiResponseGenerator(apiAiClient: ApiAiClient) extends ResponseGenerator {

  def generateResponse(recipientMessage: Message, recipient: Recipient): BotResponse = {

    apiAiClient.generateAnswer(recipientMessage.text, recipient.id)
      .map { response =>
        val action = response.result.action match {
          case ApiAiActions.cocktail_byalcohol =>
            response.result.parameters.getOrElse(Map.empty).get(ApiAiParameters.alcoholType) match {
              case Some(alcoholType) =>
                if (alcoholType.nonEmpty) Some(BotAction.CocktailByAlcohol(alcoholType)) else None
              case None => None
            }
          case ApiAiActions.cocktail_random => Some(BotAction.CocktailReceiptRandom())
          case ApiAiActions.input_welcome => Some(BotAction.InputWelcome())
          case ApiAiActions.nearest_bar => Some(BotAction.NearestBar())
          case _ => None
        }

        val message = Message(response.result.fulfillment.speech)

        BotResponse(message, action)
      }.getOrElse(BotResponse(message = Message("I feel me terrible... I need relax.. Sorry See you tomarrow")))
  }
}
