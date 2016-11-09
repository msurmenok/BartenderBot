import model.FbMessengerResponse
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.Json

@RunWith(classOf[JUnitRunner])
class FbMessengerResponseSpec extends Specification {

  "fb response text message convert right" should {

    val actual =
      Json.parse(
        """{
        "object":"page",
        "entry":[
        {
          "id":"PAGE_ID",
          "time":1458692752478,
          "messaging":[
           {
            "sender":{
              "id":"USER_ID"
            },
            "recipient":{
              "id":"PAGE_ID"
            },
            "timestamp":1458692752478,
            "message":{
               "mid":"mid.1457764197618:41d102a3e1ae206a38",
               "seq":73,
               "text":"hello, world!"
             }
          }  ]
        }
        ]
      }""").as[FbMessengerResponse]

    actual.`object` must equalTo("page")
    actual.entry.length === 1
  }

  "fb response message with attach url convert right" should {

    val actual =
      Json.parse(
        """{
        "object":"page",
        "entry":[
        {
          "id":"PAGE_ID",
          "time":1458692752478,
          "messaging":[
          {
            "sender":{
              "id":"USER_ID"
            },
            "recipient":{
              "id":"PAGE_ID"
            },
            "timestamp":1458692752478,
            "message":{
              "mid":"mid.1458696618141:b4ef9d19ec21086067",
              "seq":51,
              "attachments":[
                {
                  "type":"image",
                  "payload":{
                    "url":"IMAGE_URL"
                  }
                }
              ]
            }
          }      ]
        }
        ]
      }""").as[FbMessengerResponse]

    actual.`object` must equalTo("page")
    actual.entry.length === 1
  }

  "fb response message with attach location convert right" should {

    val actual =
      Json.parse(
        """{
        "object":"page",
        "entry":[
        {
          "id":"PAGE_ID",
          "time":1458692752478,
          "messaging":[
          {
            "sender":{
              "id":"USER_ID"
            },
            "recipient":{
              "id":"PAGE_ID"
            },
            "timestamp":1458692752478,
            "message":{
              "mid":"mid.1458696618141:b4ef9d19ec21086067",
              "seq":51,
              "attachments":[
                {
                  "type":"location",
                  "payload":{
                    "coordinates":{"lat":0,"long":0}
                  }
                }
              ]
            }
          }      ]
        }
        ]
      }""").as[FbMessengerResponse]

    actual.`object` must equalTo("page")
    actual.entry.length === 1
  }

  "fb response text message with quick reply convert right" should {

    val actual =
      Json.parse(
        """{
        "object":"page",
        "entry":[
        {
          "id":"PAGE_ID",
          "time":1458692752478,
          "messaging":[
           {
            "sender":{
              "id":"USER_ID"
            },
            "recipient":{
              "id":"PAGE_ID"
            },
            "timestamp":1458692752478,
            "message":{
               "mid":"mid.1457764197618:41d102a3e1ae206a38",
               "seq":73,
               "text":"hello, world!",
              "quick_reply": {
                    "payload": "DEVELOPER_DEFINED_PAYLOAD"
                 }
             }
          }  ]
        }
        ]
      }""").as[FbMessengerResponse]

    actual.`object` must equalTo("page")
    actual.entry.length === 1
  }

}
