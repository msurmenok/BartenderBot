cd /opt/BartenderBot/bot-service-akka
sudo git pull origin master
sudo systemctl stop bot-service-akka
sudo sbt pack
sudo rm -rf /opt/BartenderDeploy/*
sudo cp target/pack/* /opt/BartenderDeploy -r
sudo systemctl start bot-service-akka
