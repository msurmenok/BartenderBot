package com.bartender.bot.service.fb

case class FbMessengerHookBody(`object`: String, entry: Seq[FbEntry])

case class FbEntry(id: String, time: Long, messaging: Seq[FbMessaging])

case class FbMessaging(sender: FbSender,
                       recipient: FbRecipient,
                       timestamp: Long,
                       message: Option[FbMessage] = None,
                       delivery: Option[FbServiceMessage] = None,
                       read: Option[FbServiceMessage] = None)

case class FbSender(id: String)

case class FbRecipient(id: String)

case class FbMessage(mid: String,
                     seq: Long,
                     text: Option[String] = None,
                     quick_reply: Option[FbQuickReplyResponse] = None,
                     attachments: Option[Seq[FbAttachment]] = None,
                     metadata: Option[String] = None)

case class FbQuickReplyResponse(payload: String)
case class FbServiceMessage(watermark: Long, seq: Long, mids: Option[Seq[String]])

case class QuickReplyResponse(payload: String)

case class FbAttachment(`type`: FbAttachmentType.Value,
                        payload: FbPayload)

object FbAttachmentType extends Enumeration {
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
case class FbPayload(url: Option[String] = None,
                     is_reusable: Option[Boolean] = None,
                     attachment_id: Option[String] = None,
                     coordinates: Option[FbCoordinates] = None,
                     template_type: Option[FbTemplateType.Value] = None
                     //  buttons: Option[Seq[Button]] = None
                  )

case class FbCoordinates(lat: Double, long: Double)

object FbTemplateType extends Enumeration {
  type EnumA = Value
  val generic, button, list = Value
}

//case class Button(   "type":"web_url",
//"url":"https://petersapparel.parseapp.com",
//"title":"Show Website")

//web_url, postback or phone_number


/** * request models for send API FA Messenger ***/

/** *
  * you can send JUST message OR sender_action
  *
  * @param recipient         - who send your message
  * @param message           - message object
  * @param sender_action     - you can send state messaging
  * @param notification_type - for push notification, default - REGULAR
  */
case class FbMessengerRequest(recipient: FbRecipient,
                              message: Option[FbSendMessage] = None,
                              sender_action: Option[FbSenderAction.Value] = None,
                              notification_type: Option[FbNotificationType.Value] = None)

object FbSenderAction extends Enumeration {
  type EnumA = Value
  val typing_on, typing_off, mark_seen = Value
}

object FbNotificationType extends Enumeration {
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
case class FbSendMessage(text: Option[String] = None,
                         attachment: Option[FbAttachment] = None,
                         quick_replies: Option[Seq[FbQuickReplyRequest]] = None,
                         metadata: Option[String] = None)

/** *
  *
  * @param content_type can be: text or location, optional field just for content_type - text
  * @param title        - for button
  * @param payload      - it will send you by webhook
  * @param image_url    - for button
  */
case class FbQuickReplyRequest(content_type: FbQuickReplyType.Value,
                               title: Option[String],
                               payload: Option[String],
                               image_url: Option[String])

object FbQuickReplyType extends Enumeration {
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





