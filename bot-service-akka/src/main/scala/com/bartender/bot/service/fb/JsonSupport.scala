package com.bartender.bot.service.fb

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object AttachmentTypeFormat extends RootJsonFormat[AttachmentType.Value] {
    def write(obj: AttachmentType.Value): JsValue = JsString(obj.toString)

    def read(json: JsValue): AttachmentType.Value = json match {
      case JsString(str) => AttachmentType.withName(str)
      case _ => throw DeserializationException("Enum string expected")
    }
  }

  implicit object TemplateTypeFormat extends RootJsonFormat[TemplateType.Value] {
    def write(obj: TemplateType.Value): JsValue = JsString(obj.toString)

    def read(json: JsValue): TemplateType.Value = json match {
      case JsString(str) => TemplateType.withName(str)
      case _ => throw DeserializationException("Enum string expected")
    }
  }

  implicit val coordinates = jsonFormat2(Coordinates)
  implicit val payloadFormat = jsonFormat5(Payload)
  implicit val attachmentFormat = jsonFormat2(Attachment)
  implicit val quickReplyResponseFormat = jsonFormat1(QuickReplyResponse)
  implicit val messageFormat = jsonFormat6(Message)
  implicit val recipientFormat = jsonFormat1(Recipient)
  implicit val senderFormat = jsonFormat1(Sender)
  implicit val messagingFormat = jsonFormat4(Messaging)
  implicit val entryFormat = jsonFormat3(Entry)
  implicit val fbMessengerResponseFormat = jsonFormat2(FbMessengerResponse)
}
