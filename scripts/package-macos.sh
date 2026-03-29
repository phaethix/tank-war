#!/bin/zsh
mvn clean package

jpackage \
  --name tank-war \
  --input target \
  --main-jar tank-war-1.0-SNAPSHOT.jar \
  --main-class org.tinygame.tankwar.Main \
  --type app-image \
  --java-options "--enable-preview" \
  --verbose

sudo xattr -cr tank-war.app

zip -r tank-war-macos.zip tank-war.app

rm -rf tank-war.app