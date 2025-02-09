# prayingtimes-telegrambot
A simple telegram bot  what can tell you praying time in your area

set "botName" and "botToken" values for Helm

To Build
podman run -it --rm --name my-maven-project -v "$(pwd)":/usr/src/mymaven -v "/mnt/hdd/build/maven":/root/.m2 -w /usr/src/mymaven docker.io/library/maven:3-eclipse-temurin-23-alpine mvn clean package
buildah build -t docker.io/library/map7000/prayingtimes-telegrambot:0.0.1.SNAPSHOT
podman login docker.io
buildah push map7000/prayingtimes-telegrambot:0.0.1.SNAPSHOT