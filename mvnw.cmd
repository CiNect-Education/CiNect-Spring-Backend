@REM Maven Wrapper for Windows
@REM Requires Maven to be installed or downloads the wrapper automatically
@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

if not exist %WRAPPER_JAR% (
    echo Downloading Maven wrapper...
    mkdir "%MAVEN_PROJECTBASEDIR%.mvn\wrapper" 2>NUL
    powershell -Command "Invoke-WebRequest -Uri %WRAPPER_URL% -OutFile %WRAPPER_JAR%"
)

java -jar %WRAPPER_JAR% %*
