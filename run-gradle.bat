@echo off
REM Set Java 11+ path for this session
set JAVA_HOME=C:\Program Files\Java\_jdk-20
set PATH=%JAVA_HOME%\bin;%PATH%

REM Run Gradle task in the current project directory
gradlew %*
