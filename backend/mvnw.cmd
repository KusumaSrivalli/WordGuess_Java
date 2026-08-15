@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@SETLOCAL

@SET MAVEN_PROJECTBASEDIR=%~dp0
@IF NOT "%MAVEN_PROJECTBASEDIR%"=="" @SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

@SET WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@IF EXIST %WRAPPER_JAR% GOTO run

@IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties" GOTO error

@ECHO Downloading Maven Wrapper JAR...
C:\Windows\System32\WindowsPowerShell\v1.0\powershell.exe -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar', '%WRAPPER_JAR:\=/%')"

:run
@SET "JAVA_EXE="
@IF DEFINED JAVA_HOME (
    @IF EXIST "%JAVA_HOME%\bin\java.exe" @SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
)

@IF "%JAVA_EXE%"=="" (
    @FOR /F "tokens=*" %%I IN ('where java.exe 2^>nul') DO @SET "JAVA_EXE=%%I"
)

@IF "%JAVA_EXE%"=="" (
    @ECHO [ERROR] java.exe could not be found!
    @ECHO Please ensure Java 17+ is installed and JAVA_HOME environment variable is set.
    @EXIT /B 1
)

@ECHO Using Java: "%JAVA_EXE%"
"%JAVA_EXE%" -classpath %WRAPPER_JAR% "-Dmaven.home=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" %WRAPPER_LAUNCHER% %*

@IF ERRORLEVEL 1 (
    @ECHO Maven Wrapper returned error code %ERRORLEVEL%
    @EXIT /B %ERRORLEVEL%
)
@EXIT /B 0

:error
@ECHO Error: Could not find maven-wrapper.properties
@EXIT /B 1
