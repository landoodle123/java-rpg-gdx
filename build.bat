@echo off
setlocal enabledelayedexpansion

:: ============================================================
::  build_all.bat
::  Builds Windows / Linux / Mac / Other JARs via Gradle and
::  copies map JSON files into each platform output folder.
:: ============================================================

set "BASE=C:\Users\lando\OneDrive\Documents\java-rpg\java-rpg-gdx"
set "LIBS=%BASE%\lwjgl3\build\libs"
set "MAPS=%BASE%\assets"

:: JSON files to copy into every platform folder
set "JSON_FILES=dungeon1.json dungeon2p1.json dungeon2p2.json dungeon2p3.json dungeon3.json finalBoss.json main.json upgradeRoom.json"

echo ============================================================
echo  Building all platform JARs
echo ============================================================
echo.

:: ---- Windows ------------------------------------------------
echo [1/4] Building Windows JAR...
call "%BASE%\gradlew.bat" jarWin
if errorlevel 1 (
    echo ERROR: Windows build failed.
    exit /b 1
)
echo Windows build succeeded.
echo.

:: ---- Linux --------------------------------------------------
echo [2/4] Building Linux JAR...
call "%BASE%\gradlew.bat" jarLinux
if errorlevel 1 (
    echo ERROR: Linux build failed.
    exit /b 1
)
echo Linux build succeeded.
echo.

:: ---- Mac ----------------------------------------------------
echo [3/4] Building Mac JAR...
call "%BASE%\gradlew.bat" jarMac
if errorlevel 1 (
    echo ERROR: Mac build failed.
    exit /b 1
)
echo Mac build succeeded.
echo.

:: ---- Other / Generic ----------------------------------------
echo [4/4] Building generic JAR...
call "%BASE%\gradlew.bat" jar
if errorlevel 1 (
    echo ERROR: Generic build failed.
    exit /b 1
)
echo Generic build succeeded.
echo.

:: ============================================================
::  Create output folders and copy JSON files
:: ============================================================
echo Copying JSON files to platform folders...
echo.

for %%P in (windows linux mac other) do (
    set "DEST=%LIBS%\%%P"
    if not exist "!DEST!" (
        mkdir "!DEST!"
    )
    for %%F in (%JSON_FILES%) do (
        if exist "%MAPS%\%%F" (
            copy /Y "%MAPS%\%%F" "!DEST!\%%F" >nul
            echo   Copied %%F -> %%P\
        ) else (
            echo   WARNING: %%F not found at %MAPS%, skipping.
        )
    )
)

echo.
echo ============================================================
echo  All builds complete. Output folders:
echo    %LIBS%\windows
echo    %LIBS%\linux
echo    %LIBS%\mac
echo    %LIBS%\other
echo ============================================================

endlocal
pause
