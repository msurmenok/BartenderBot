rm -rf /opt/BartenderBuildWorkspace/*
git pull
cp ./botservice-akka/* /opt/BartenderBuildWorkspace
rm /opt/BartenderBuildWorkspace/src/main/resources/application.conf
cp /opt/BartenderConfig/application.config /opt/BartenderBuildWorkspace/src/main/resources
cd /opt/BartenderBuildWorkspace
sudo systemctl stop bot-service-akka
sbt pack
rm -rf /opt/BartenderDeploy/*
cp target/pack/* /opt/BartenderDeploy -r
sudo systemctl start bot-service-akka
