@echo off
REM Set Java 11+ path for this session
set JAVA_HOME=C:\Program Files\Java\_jdk-20
set PATH=%JAVA_HOME%\bin;%PATH%

REM Run Gradle with a heartbeat line every 30 s (see tools\gradle-heartbeat.ps1)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\gradle-heartbeat.ps1" %*
exit /b %ERRORLEVEL%
