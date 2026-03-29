@echo off
setlocal

set "PROJECT_ROOT=%~dp0.."
cd /d "%PROJECT_ROOT%"

set "JAR_PATH=target\tank-war-1.0-SNAPSHOT.jar"

call mvn clean package
if errorlevel 1 exit /b %errorlevel%

java --enable-preview -jar "%JAR_PATH%"
