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
}