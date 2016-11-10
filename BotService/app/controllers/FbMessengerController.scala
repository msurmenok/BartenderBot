package controllers

import model._
import play.api.Logger
import play.api.mvc._

class FbMessengerController extends Controller {

  def webhook = Action(parse.json) { request =>
    Logger.info("webhook init")
    request.body.validate(FbMessengerResponse.fbMessengerResponseReads).map { entity =>
      Logger.info("user message: " + entity.entry.head.messaging.head.message.text.head)
      Ok
    }.recoverTotal {
      result => BadRequest
    }
  }

  def verifyWebhook(`hub.mode`: Option[String], `hub.verify_token`: Option[String], `hub.challenge`: Option[String]) = Action {

    val verify_token = Some("VERIFY_TOKEN") //todo get real token from config

    if (`hub.mode`.getOrElse("") == "subscribe" && `hub.verify_token` == verify_token) {
      Logger.info("Validating webhook")
      Ok(`hub.challenge`.getOrElse(""))
    }
    else {
      Logger.error("Failed validation. Make sure the validation tokens match.")
      Forbidden
    }
  }
}