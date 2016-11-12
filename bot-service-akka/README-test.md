
**Start server from terminal: **
           
           bot-service-akka user$ sbt run


http://localhost:9000/v1/info


**Test fb messenger webhook in terminal:**

**validate:**
   
    http://localhost:9000/v1/fb-messenger-webhook?hub.mode=subscribe&hub.verify_token=VERIFY_TOKEN&hub.challenge=%22challenge%22

**send event:**

curl -X POST -H "Content-Type: application/json" --data-binary @- http://localhost:9000/v1/fb-messenger-webhook <<EOF

{
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
      }
      
EOF

result in Log:
[info] application - webhook init
[info] application - user message: hello, world!
