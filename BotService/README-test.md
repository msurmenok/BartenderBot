
Start server from terminal:
1. sbt
2. run


Test fb messenger webhook in terminal:

curl -X POST -H "Content-Type: application/json" --data-binary @- http://localhost:9000/fb-messenger-webhook <<EOF

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
