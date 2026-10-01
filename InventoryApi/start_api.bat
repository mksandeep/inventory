@echo off
REM Build and run the API

echo Building API...
cd /d C:\Users\mksan\git\inventory\InventoryApi
call .\gradlew.bat clean build

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build successful! Starting API...
    echo.
    java -jar build\libs\InventoryApi-1.0-SNAPSHOT.jar
) else (
    echo.
    echo Build failed!
    pause
)

