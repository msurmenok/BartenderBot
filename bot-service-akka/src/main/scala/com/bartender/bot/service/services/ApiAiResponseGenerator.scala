package com.bartender.bot.service.services

import com.bartender.bot.service.api.ai.ApiAiClient
import com.bartender.bot.service.api.ai.ApiAiModel.ApiAiActions
import com.bartender.bot.service.domain.{BotActions, BotResponse, Message, Recipient}

class ApiAiResponseGenerator(apiAiClient: ApiAiClient) extends ResponseGenerator {

  def generateResponse(recipientMessage: Message, recipient: Recipient): BotResponse = {

     apiAiClient.generateAnswer(recipientMessage.text, recipient.id)
      .map{response =>
        val action = response.result.action match {
          case ApiAiActions.cocktail_byalcohol => Some(BotActions.COCKTAIL_BY_ALCOHOL)
          case ApiAiActions.cocktail_random => Some(BotActions.COCKTAIL_RANDOM)
          case ApiAiActions.input_welcome => Some(BotActions.INPUT_WELCOME)
          case ApiAiActions.nearest_bar => Some(BotActions.NEAREST_BAR)
          case _ => None
        }

        val message = Message(response.result.fulfillment.speech)

        BotResponse(message, action)
      }.getOrElse(BotResponse(message = Message("I feel me terrible... I need relax.. Sorry See you tomarrow")))
  }
}
