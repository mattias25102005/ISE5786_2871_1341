@echo off
rem generate-javadoc.bat - generates Javadoc while suppressing doclint warnings
setlocal enabledelayedexpansion
echo Collecting Java sources...
if exist "%~dp0sources.txt" del /q "%~dp0sources.txt"
for /f "delims=" %%F in ('dir /b /s "%~dp0src\*.java"') do @echo %%F>>"%~dp0sources.txt"
if not exist "%~dp0sources.txt" (
  echo No Java source files found in "%~dp0src" && exit /b 1
)
echo Generating Javadoc to "%~dp0out\javadoc" (doclint disabled)...
if exist "%~dp0out\javadoc" rd /s /q "%~dp0out\javadoc"
javadoc -d "%~dp0out\javadoc" -encoding UTF-8 -charset UTF-8 -quiet -Xdoclint:none @"%~dp0sources.txt"nerrorlevel %errorlevel% && echo Javadoc finished with code %errorlevel%
del /q "%~dp0sources.txt"
endlocal
echo Done.