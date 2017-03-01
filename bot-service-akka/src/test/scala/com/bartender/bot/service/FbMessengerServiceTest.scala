package com.bartender.bot.service

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.ValidationRejection
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bartender.bot.service.common.Config
import com.bartender.bot.service.domain.{Bar, Location, Message, Recipient}
import com.bartender.bot.service.fb.{FbJsonSupport, FbMessengerService}
import com.bartender.bot.service.services.MessageReceiver
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}

// TODO: use mockito
class MessageReceiverStub extends MessageReceiver {
  var text: String = _
  var nearestBar: Boolean = false
  var barDetails: Boolean = false

  def receive(message: Message, recipient: Recipient) {
    this.text = message.text
  }

  def receiveNearestBar(location: Location, recipient: Recipient, offset: Int): Option[Bar] ={
    nearestBar = true
    Some(Bar("id", "name", Some("address"), Some("photoUrl"), location))
  }


  def receiveBarDetails(barId: String, recipient: Recipient) {
    barDetails = true
  }

  override def receiveCoctailsByAlcohol(alcohol: String, recipient: Recipient, offset: Int): Unit = ???

  override def receiveCocktailReceipt(cocktailId: String, recipient: Recipient): Unit = ???
}

class FbMessengerServiceTest extends WordSpec with Matchers with ScalatestRouteTest with FbJsonSupport with Config  with BeforeAndAfterEach {

  var receiver: MessageReceiverStub = _
  var fbMessengerService: FbMessengerService = _

  override def beforeEach(): Unit = {
    receiver = new MessageReceiverStub()
    fbMessengerService = new FbMessengerService(receiver)
  }

  "Facebook messenger webhook" should {

    val webhookPath = "/" + fbMessengerWebhook

    "validate verify token" in {
      val challenge = "challenge"
      val path = webhookPath + s"?hub.mode=subscribe&hub.verify_token=$fbMessengerVerifyToken&hub.challenge=$challenge"
      Get(path) ~> fbMessengerService.route ~> check {
        responseAs[String] shouldEqual challenge
      }
    }


    "validate not verify token" in {
      Get(webhookPath + s"?hub.mode=subscribe&hub.verify_token=not_valid_token") ~> fbMessengerService.route ~> check {
        rejection shouldEqual ValidationRejection("Verify token not correct!")
      }
    }

    "receive text message" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, textMessageRequest)
      Post(webhookPath, requestEntity) ~> fbMessengerService.route ~> check {
        status.isSuccess() shouldEqual true
        receiver.text shouldEqual "hello, world!"
      }
    }

    "receive attachments message" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, attachmentsMessageRequest)
      Post(webhookPath, requestEntity) ~> fbMessengerService.route ~> check {
        status.isSuccess() shouldEqual true
        //todo check receive not not implement
      }
    }

    "receive location message" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, locationMessageRequest)
      Post(webhookPath, requestEntity) ~> fbMessengerService.route ~> check {
        status.isSuccess() shouldEqual true
        receiver.nearestBar shouldEqual true
      }
    }

    "receive quick reply message" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, quickReplyMessageRequest)
      Post(webhookPath, requestEntity) ~> fbMessengerService.route ~> check {
        status.isSuccess() shouldEqual true
        receiver.text shouldEqual "hello, world!"
      }
    }

    "receive postback show new nearest bar" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, postShowNextNearestBar)
      Post(webhookPath, requestEntity) ~> fbMessengerService.route ~> check {
        status.isSuccess() shouldEqual true
        receiver.nearestBar shouldEqual true
      }
    }

    "receive postback details bar" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, postBackBarDetails)
      Post(webhookPath, requestEntity) ~> fbMessengerService.route ~> check {
        status.isSuccess() shouldEqual true
        receiver.barDetails shouldEqual true
      }
    }

    "request 1 was be parsed" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, request1)
      Post(webhookPath, requestEntity) ~> fbMessengerService.route ~> check {
        status.isSuccess() shouldEqual true
      }
    }

    "request 2 was be parsed" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, request2)
      Post(webhookPath, requestEntity) ~> fbMessengerService.route ~> check {
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
                    "coordinates":{"lat":55.695738,"long":37.624188}
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


  val postShowNextNearestBar = ByteString(
    """
      |{
      |   "object":"page",
      |   "entry":[
      |      {
      |         "id":"PAGE_ID",
      |         "time":1479158759114,
      |         "messaging":[
      |            {
      |               "sender":{
      |                  "id":"USER_ID"
      |               },
      |               "recipient":{
      |                  "id":"PAGE_ID"
      |               },
      |               "timestamp":0,
      |               "postback":{
      |                  "payload":"Show one more;13.732900;100.562810;1"
      |               }
      |            }
      |         ]
      |      }
      |   ]
      |}
      |    """.stripMargin)


  val postBackBarDetails = ByteString(
    """
      {
      |   "object":"page",
      |   "entry":[
      |      {
      |         "id":"PAGE_ID",
      |         "time":1479158759114,
      |         "messaging":[
      |            {
      |               "sender":{
      |                  "id":"USER_ID"
      |               },
      |               "recipient":{
      |                  "id":"PAGE_ID"
      |               },
      |               "timestamp":0,
      |               "postback":{
      |                  "payload":"Details;ChIJN1t_tDeuEmsRUsoyG83frY4"
      |               }
      |            }
      |         ]
      |      }
      |   ]
      |}
      |    """.stripMargin)

  val request1 = ByteString(
    """
      |{
      |   "object":"page",
      |   "entry":[
      |      {
      |         "id":"PAGE_ID",
      |         "time":1479158759114,
      |         "messaging":[
      |            {
      |               "sender":{
      |                  "id":"USER_ID"
      |               },
      |               "recipient":{
      |                  "id":"PAGE_ID"
      |               },
      |               "timestamp":0,
      |               "delivery":{
      |                  "mids":[
      |                     "mid.1479148107873:42ae3f4345"
      |                  ],
      |                  "watermark":1479148107873,
      |                  "seq":25
      |               }
      |            }
      |         ]
      |      }
      |   ]
      |}
      |    """.stripMargin)

  val request2 = ByteString(
    """
      {
         "object":"page",
         "entry":[
            {
               "id":"PAGE_ID",
               "time":1479158760373,
               "messaging":[
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":1479148108302,
                     "read":{
                        "watermark":1479148107873,
                        "seq":26
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":1479148118031,
                     "message":{
                        "mid":"mid.1479148118031:785b4a4029",
                        "seq":27,
                        "text":"How are you?"
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":1479148150367,
                     "message":{
                        "mid":"mid.1479148150367:70178a2015",
                        "seq":28,
                        "text":"Q"
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":1479148373753,
                     "message":{
                        "mid":"mid.1479148373753:bf21306805",
                        "seq":29,
                        "text":"hi"
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":0,
                     "delivery":{
                        "mids":[
                           "mid.1479148107873:42ae3f4345"
                        ],
                        "watermark":1479148107873,
                        "seq":30
                     }
                  },
                  {
                     "sender":{
                        "id":"PAGE_ID"
                     },
                     "recipient":{
                        "id":"USER_ID"
                     },
                     "timestamp":1479148529054,
                     "message":{
                        "is_echo":true,
                        "app_id":368836740120344,
                        "mid":"mid.1479148529054:fa44d2bb38",
                        "seq":31,
                        "text":"Hi!, How are you?"
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":0,
                     "delivery":{
                        "mids":[
                           "mid.1479148529054:fa44d2bb38"
                        ],
                        "watermark":1479148529054,
                        "seq":32
                     }
                  },
                  {
                     "sender":{
                        "id":"PAGE_ID"
                     },
                     "recipient":{
                        "id":"USER_ID"
                     },
                     "timestamp":1479148533772,
                     "message":{
                        "is_echo":true,
                        "app_id":368836740120344,
                        "mid":"mid.1479148533772:1ded5e8031",
                        "seq":33,
                        "text":"Hi!, How are you?"
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":0,
                     "delivery":{
                        "mids":[
                           "mid.1479148533772:1ded5e8031"
                        ],
                        "watermark":1479148533772,
                        "seq":34
                     }
                  },
                  {
                     "sender":{
                        "id":"PAGE_ID"
                     },
                     "recipient":{
                        "id":"USER_ID"
                     },
                     "timestamp":1479148537513,
                     "message":{
                        "is_echo":true,
                        "app_id":368836740120344,
                        "mid":"mid.1479148537513:ac5743af82",
                        "seq":35,
                        "text":"Hi!, How are you?"
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":0,
                     "delivery":{
                        "mids":[
                           "mid.1479148537513:ac5743af82"
                        ],
                        "watermark":1479148537513,
                        "seq":36
                     }
                  },
                  {
                     "sender":{
                        "id":"PAGE_ID"
                     },
                     "recipient":{
                        "id":"USER_ID"
                     },
                     "timestamp":1479148938164,
                     "message":{
                        "is_echo":true,
                        "app_id":368836740120344,
                        "mid":"mid.1479148938164:06ed4bea39",
                        "seq":37,
                        "text":"Hi!, How are you?"
                     }
                  },
                  {
                     "sender":{
                        "id":"PAGE_ID"
                     },
                     "recipient":{
                        "id":"USER_ID"
                     },
                     "timestamp":1479148942697,
                     "message":{
                        "is_echo":true,
                        "app_id":368836740120344,
                        "mid":"mid.1479148942697:dee1b10405",
                        "seq":38,
                        "text":"Hi!, How are you?"
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":0,
                     "delivery":{
                        "mids":[
                           "mid.1479148938164:06ed4bea39",
                           "mid.1479148942697:dee1b10405"
                        ],
                        "watermark":1479148942697,
                        "seq":39
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":0,
                     "delivery":{
                        "mids":[
                           "mid.1479148938164:06ed4bea39",
                           "mid.1479148942697:dee1b10405"
                        ],
                        "watermark":1479148942697,
                        "seq":42
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":1479149752342,
                     "read":{
                        "watermark":1479148942697,
                        "seq":43
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":1479149756326,
                     "message":{
                        "mid":"mid.1479149756326:e56b810506",
                        "seq":44,
                        "text":"Hi"
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":1479149764543,
                     "message":{
                        "mid":"mid.1479149764543:ebe6677d22",
                        "seq":45,
                        "text":"Hey"
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":1479150026149,
                     "message":{
                        "mid":"mid.1479150026149:61f7b2d228",
                        "seq":46,
                        "text":"f"
                     }
                  },
                  {
                     "sender":{
                        "id":"PAGE_ID"
                     },
                     "recipient":{
                        "id":"USER_ID"
                     },
                     "timestamp":1479150207879,
                     "message":{
                        "is_echo":true,
                        "mid":"mid.1479150207879:6c8f570d07",
                        "seq":49,
                        "text":"Hey"
                     }
                  },
                  {
                     "sender":{
                        "id":"USER_ID"
                     },
                     "recipient":{
                        "id":"PAGE_ID"
                     },
                     "timestamp":0,
                     "delivery":{
                        "mids":[
                           "mid.1479150207879:6c8f570d07"
                        ],
                        "watermark":1479150207879,
                        "seq":50
                     }
                  }
               ]
            }
         ]
      }    """.stripMargin)
}
