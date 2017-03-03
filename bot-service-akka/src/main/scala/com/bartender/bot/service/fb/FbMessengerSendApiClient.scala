package com.bartender.bot.service.fb

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import com.bartender.bot.service.common.{Config, Logging}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait FbMessengerSendApiClient {
  def sendTextMessage(recipient: FbRecipient, text: String): Unit

  def sendTemplateMessage(recipient: FbRecipient, elements: FbTemplateElement*): Unit

  def sendListTemplateMessage(recipient: FbRecipient, elements: Seq[FbTemplateElement], button: Option[FbTemplateButton] = None): Unit
}

class FbMessengerSendApiClientHttp extends FbMessengerSendApiClient with FbJsonSupport with Config with Logging {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  def sendTextMessage(recipient: FbRecipient, text: String): Unit = {
    rootLogger.info(s"sending message to recipient(${recipient.id}): $text")
    val body = FbMessengerRequest(recipient, Some(FbSendMessage(Some(text))))
    val response = sendFbMessengerRequest(body)
    rootLogger.info(s"result message sending: ${response.status}")
    rootLogger.info(s"${response.entity}")
  }

  def sendTemplateMessage(recipient: FbRecipient, elements: FbTemplateElement*): Unit = {
    rootLogger.info(s"sending template message to recipient(${recipient.id})")
    val payload = FbPayload(elements = Some(elements), template_type = Some(FbTemplateType.generic))
    val attachment = FbAttachment(FbAttachmentType.template, payload = payload)
    val body = FbMessengerRequest(recipient, Some(FbSendMessage(attachment = Some(attachment))))
    val response = sendFbMessengerRequest(body)
    rootLogger.info(s"result message sending: ${response.status}")
    rootLogger.info(s"${response.entity}")
  }

  def sendListTemplateMessage(recipient: FbRecipient, elements: Seq[FbTemplateElement], button: Option[FbTemplateButton] = None): Unit = {
    rootLogger.info(s"sending list template message to recipient(${recipient.id})")
    val payload = FbPayload(
      elements = Some(elements),
      buttons = button.map(b => Seq(b)),
      template_type = Some(FbTemplateType.list),
      top_element_style = Some(FbTopElementListTemplateType.compact))
    val attachment = FbAttachment(FbAttachmentType.template, payload = payload)
    val body = FbMessengerRequest(recipient, Some(FbSendMessage(attachment = Some(attachment))))
    val response = sendFbMessengerRequest(body)
    rootLogger.info(s"result message sending: ${response.status}")
    rootLogger.info(s"${response.entity}")
  }

  private def sendFbMessengerRequest(body: FbMessengerRequest): HttpResponse = {
    val httpRequest = HttpRequest(method = HttpMethods.POST, uri = fbSendApiUrl,
      entity = HttpEntity(ContentTypes.`application/json`, fbMessengerRequestFormat.write(body).compactPrint))
    val fResponse = Http().singleRequest(httpRequest)
    Await.result(fResponse, Duration.Inf)
  }
}
