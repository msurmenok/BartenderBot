package com.bartender.bot.service.fb

case class FbMessengerResponse(`object`: String, entry: Seq[Entry])

case class Entry(id: String, time: Long, messaging: Seq[Messaging])

case class Messaging(sender: User, recipient: User, timestamp: Long, message: Option[Message] = None)

case class User(id: String)

case class Message(mid: String,
                   seq: Long,
                   text: Option[String] = None,
                   quick_reply: Option[QuickReplyResponse] = None,
                   attachments: Option[Seq[Attachment]] = None,
                   metadata: Option[String] = None)

case class QuickReplyResponse(payload: String)

case class Attachment(`type`: AttachmentType.Value,
                      payload: Payload)

object AttachmentType extends Enumeration {
  type EnumA = Value
  val image, audio, video, file, location, template = Value
}

/** *
  * for attachment content in message
  *
  * @param url           - use for attachment type - image, audio, video, file
  * @param is_reusable   - set true for optimisation send a lot of same attachment
  * @param attachment_id - after use is_reusable == true, you will have it parament for optimisation
  * @param coordinates   - for attachment type: location (just for response in webhook)
  *
  *                      // template (just for send API)
  */
case class Payload(url: Option[String] = None,
                   is_reusable: Option[Boolean] = None,
                   attachment_id: Option[String] = None,
                   coordinates: Option[Coordinates] = None,
                   template_type: Option[TemplateType.Value] = None
                   //  buttons: Option[Seq[Button]] = None
                  )

case class Coordinates(lat: Long, long: Long)

object TemplateType extends Enumeration {
  type EnumA = Value
  val generic, button, list = Value
}

//case class Button(   "type":"web_url",
//"url":"https://petersapparel.parseapp.com",
//"title":"Show Website")

//web_url, postback или phone_number


/** * request models for send API FA Messenger ***/

/** *
  * you can send JUST message OR sender_action
  *
  * @param recipient         - who send your message
  * @param message           - message object
  * @param sender_action     - you can send state messaging
  * @param notification_type - for push notification, default - REGULAR
  */
case class FbMessengerRequest(recipient: User,
                              message: Option[SendMessage] = None,
                              sender_action: Option[SenderAction.Value] = None,
                              notification_type: Option[NotificationType.Value] = None)

object SenderAction extends Enumeration {
  type EnumA = Value
  val typing_on, typing_off, mark_seen = Value
}

object NotificationType extends Enumeration {
  type EnumA = Value
  val REGULAR, SILENT_PUSH, NO_PUSH = Value
}

/** *
  * you can send JUST text OR attachment
  *
  * @param text          - for send text message max 320 char
  * @param attachment    - for send image, audio, video, file or template
  * @param quick_replies - you can help user answer on your question
  * @param metadata      - you receive it in your Webhook max 1000 char
  */
case class SendMessage(text: Option[String] = None,
                       attachment: Option[Attachment] = None,
                       quick_replies: Option[Seq[QuickReplyRequest]] = None,
                       metadata: Option[String] = None)

/** *
  *
  * @param content_type can be: text or location, optional field just for content_type - text
  * @param title        - for button
  * @param payload      - it will send you by webhook
  * @param image_url    - for button
  */
case class QuickReplyRequest(content_type: QuickReplyType.Value,
                             title: Option[String],
                             payload: Option[String],
                             image_url: Option[String])

object QuickReplyType extends Enumeration {
  type EnumA = Value
  val text, location = Value
}


/** *
  * send API response
  */
//{
//  "recipient_id": "USER_ID",
//  "message_id": "mid.1473372944816:94f72b88c597657974",
//  "attachment_id": "1745504518999123"
//}


//{
//  "error": {
//  "message": "Invalid OAuth access token.",
//  "type": "OAuthException",
//  "code": 190,
//  "fbtrace_id": "BLBz/WZt8dN"
//}
//}





