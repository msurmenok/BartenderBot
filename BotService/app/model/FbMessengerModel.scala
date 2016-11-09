package model

import play.api.libs.json.Json


case class Coordinates(lat: Long, long: Long)

object Coordinates {
  implicit val coordinatesReads = Json.reads[Coordinates]
}

case class Payload(
                    url: Option[String] = None,
                    coordinates: Option[Coordinates] = None
                  )

object Payload {
  implicit val payloadReads = Json.reads[Payload]
}


case class Attachments(
                        `type`: String, //image, audio, video, file - url, location - coordinates
                        payload: Payload
                      )

object Attachments {
  implicit val attachmentsReads = Json.reads[Attachments]
}


case class QuickReply(payload: String)

object QuickReply {
  implicit val quickReplyReads = Json.reads[QuickReply]
}

case class Message(
                    mid: String,
                    seq: Long,
                    text: Option[String] = None,
                    quick_reply: Option[QuickReply] = None,
                    attachments: Option[Seq[Attachments]] = None)

object Message {
  implicit val messageReads = Json.reads[Message]
}

case class Sender(id: String)

object Sender {
  implicit val senderReads = Json.reads[Sender]
}

case class Recipient(id: String)

object Recipient {
  implicit val recipientReads = Json.reads[Recipient]
}

case class Messaging(
                      sender: Sender,
                      recipient: Recipient,
                      timestamp: Long,
                      message: Message
                    )

object Messaging {
  implicit val messagingReads = Json.reads[Messaging]
}

case class Entry(
                  id: String,
                  time: Long,
                  messaging: Seq[Messaging]
                )

object Entry {
  implicit val entryReads = Json.reads[Entry]
}

case class FbMessengerResponse(`object`: String, entry: Seq[Entry])

object FbMessengerResponse {
  implicit val fbMessengerResponseReads = Json.reads[FbMessengerResponse]
}

