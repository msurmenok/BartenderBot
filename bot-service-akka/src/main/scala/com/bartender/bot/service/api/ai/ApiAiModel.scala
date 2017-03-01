package com.bartender.bot.service.api.ai

object ApiAiModel {

  object ApiAiParameters {
    val alcoholType = "alcohol_type"
  }

  object ApiAiActions {
    val nearest_bar = "nearest.bar"
    val input_welcome = "input.welcome"
    val cocktail_byalcohol = "cocktail.byalcohol"
    val cocktail_random = "cocktail.random"
  }

  object ApiAiLanguages {
    val english = "en"
  }

  case class ApiAiMetadata(
                            intentId: Option[String],
                            webhookUsed: Option[String],
                            webhookForSlotFillingUsed: Option[String],
                            intentName: Option[String]
                          )

  case class ApiAiMessage(
                           speech: Option[String],
                           `type`: Int //0 - text -> speech non empty
                         )

  case class ApiAiFulfillment(
                               speech: String,
                               messages: Option[Seq[ApiAiMessage]]
                             )

  case class ApiApiContext(
                            name: String,
                            parameters: Option[Map[String, String]],
                            lifespan: Long
                          )

  case class ApiAiResult(
                          source: String,
                          resolvedQuery: String,
                          action: String,
                          actionIncomplete: Option[Boolean],
                          parameters: Option[Map[String, String]],
                          contexts: Option[Seq[ApiApiContext]],
                          metadata: Option[ApiAiMetadata],
                          fulfillment: ApiAiFulfillment,
                          score: Double
                        )

  case class ApiAiStatus(
                          code: Int,
                          errorType: String
                        )

  case class ApiAiResponse(
                            id: String,
                            timestamp: String,
                            lang: Option[String],
                            result: ApiAiResult,
                            status: ApiAiStatus,
                            sessionId: String
                          )

  case class ApiAiLocation(
                            latitude: Double,
                            longitude: Double
                          )

  case class ApiAiRequest(
                           query: Seq[String],
                           contexts: Option[Seq[ApiApiContext]] = None,
                           location: Option[ApiAiLocation] = None,
                           timezone: Option[String] = None,
                           lang: String = ApiAiLanguages.english,
                           sessionId: String
                         )

}
