@echo off
setlocal
set "ROOT=%~dp0.."
set "APK=%ROOT%\app\build\outputs\apk\debug\app-debug.apk"

echo ========================================
echo Build and Upload Debug APK
echo ========================================
echo.

echo [1/3] Building debug APK...
cd /d "%ROOT%"
call "%ROOT%\run-gradle.bat" assembleDebug
if errorlevel 1 (
    echo.
    echo ERROR: Build failed!
    exit /b 1
)

if not exist "%APK%" (
    echo.
    echo ERROR: APK not found at "%APK%"
    exit /b 1
)

echo [2/3] Staging APK as thumbkey.apk...
if not exist "%~dp0upload" mkdir "%~dp0upload"
copy /y "%APK%" "%~dp0upload\thumbkey.apk" >nul
if errorlevel 1 (
    echo ERROR: Failed to stage APK!
    exit /b 1
)

echo [3/3] Uploading to FTP...
call "%~dp0upload_apk_to_ftp.bat"
if errorlevel 1 (
    echo.
    echo ERROR: Upload failed!
    exit /b 1
)

echo.
echo ========================================
echo Done.
echo ========================================
echo.
endlocal
