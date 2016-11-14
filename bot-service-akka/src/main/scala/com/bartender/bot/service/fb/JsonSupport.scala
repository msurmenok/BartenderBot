package com.bartender.bot.service.fb

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object AttachmentTypeFormat extends RootJsonFormat[AttachmentType.Value] { //todo: maybe possible create common method for parse enum
    def write(obj: AttachmentType.Value): JsValue = JsString(obj.toString)

    def read(json: JsValue): AttachmentType.Value = json match {
      case JsString(str) => AttachmentType.withName(str)
      case _ => throw DeserializationException("Enum string expected")
    }
  }

  implicit object TemplateTypeFormat extends RootJsonFormat[TemplateType.Value] { //todo: maybe possible create common method for parse enum
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


  /**** send api ****/

  implicit object QuickReplyTypeFormat extends RootJsonFormat[QuickReplyType.Value] { //todo: maybe possible create common method for parse enum
  def write(obj: QuickReplyType.Value): JsValue = JsString(obj.toString)

    def read(json: JsValue): QuickReplyType.Value = json match {
      case JsString(str) => QuickReplyType.withName(str)
      case _ => throw DeserializationException("Enum string expected")
    }
  }

  implicit object NotificationTypeFormat extends RootJsonFormat[NotificationType.Value] { //todo: maybe possible create common method for parse enum
  def write(obj: NotificationType.Value): JsValue = JsString(obj.toString)

    def read(json: JsValue): NotificationType.Value = json match {
      case JsString(str) => NotificationType.withName(str)
      case _ => throw DeserializationException("Enum string expected")
    }
  }

  implicit object SenderActionFormat extends RootJsonFormat[SenderAction.Value] { //todo: maybe possible create common method for parse enum
  def write(obj: SenderAction.Value): JsValue = JsString(obj.toString)

    def read(json: JsValue): SenderAction.Value = json match {
      case JsString(str) => SenderAction.withName(str)
      case _ => throw DeserializationException("Enum string expected")
    }
  }

  implicit val quickReplyRequestFormat = jsonFormat4(QuickReplyRequest)
  implicit val sendMessageFormat = jsonFormat4(SendMessage)
  implicit val fbMessengerRequestFormat = jsonFormat4(FbMessengerRequest)

}
