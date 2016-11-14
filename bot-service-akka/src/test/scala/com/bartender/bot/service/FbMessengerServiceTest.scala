package com.bartender.bot.service

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.ValidationRejection
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bartender.bot.service.common.Config
import com.bartender.bot.service.fb.{Coordinates, FbMessengerService, JsonSupport}
import org.scalatest.{Matchers, WordSpec}

class FbMessengerServiceTest extends WordSpec with Matchers with ScalatestRouteTest with JsonSupport with Config {

  "Facebook messenger webhook" should {

    val webhookPath = "/" + fbMessengerWebhook

    "validate verify token" in {
      val challenge = "challenge"
      val path = webhookPath + s"?hub.mode=subscribe&hub.verify_token=$fbMessengerVerifyToken&hub.challenge=$challenge"
      Get(path) ~> FbMessengerService.route ~> check {
        responseAs[String] shouldEqual challenge
      }
    }


    "validate not verify token" in {
      Get(webhookPath + s"?hub.mode=subscribe&hub.verify_token=not_valid_token") ~> FbMessengerService.route ~> check {
        rejection shouldEqual ValidationRejection("Verify token not correct!")
      }
    }

    "receive text message" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, textMessageRequest)
      Post(webhookPath, requestEntity) ~> FbMessengerService.route ~> check {
        responseAs[String] shouldEqual "hello, world!"
      }
    }

    "receive attachments message" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, attachmentsMessageRequest)
      Post(webhookPath, requestEntity) ~> FbMessengerService.route ~> check {
        responseAs[String] shouldEqual "IMAGE_URL"
      }
    }

    "receive location message" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, locationMessageRequest)
      Post(webhookPath, requestEntity) ~> FbMessengerService.route ~> check {
        responseAs[String] shouldEqual Coordinates(lat = 0, long = 0).toString
      }
    }

    "receive quick reply message" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, quickReplyMessageRequest)
      Post(webhookPath, requestEntity) ~> FbMessengerService.route ~> check {
        status.isSuccess() shouldEqual true
      }
    }
  }


  val textMessageRequest = ByteString(
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
      }""".stripMargin)

  val attachmentsMessageRequest = ByteString(
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
          }]
        }
        ]
      }""".stripMargin)

  val locationMessageRequest = ByteString(
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
          }]
        }
        ]
      }""".stripMargin)

  val quickReplyMessageRequest = ByteString(
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
      }""".stripMargin)
}
