@echo off
SETLOCAL

if NOT DEFINED JAVA_HOME goto err

SET JVM=%JAVA_HOME%\jre\bin\server\jvm.dll
SET PR_PATH=%CD%
SET PR_SERVICE_NAME=ESCurator
SET PR_DISPLAY_NAME=ElasticSearch indexes curator
SET PR_JAR=lib\${project.build.finalName}-${version}.jar
SET START_CLASS=dapicard.elasticsearch.curator.Curator
SET START_METHOD=main
SET STOP_CLASS=java.lang.System
SET STOP_METHOD=exit
rem ; separated values
SET LOG_PATH=%CD%\logs
SET PID_FILE=service.pid
SET JVM_OPTIONS=-DlogPath="%LOG_PATH%"
rem ; separated values
SET STOP_PARAMS=0

bin\prunsrv.exe //IS//%PR_SERVICE_NAME% ^
   --DisplayName="%PR_DISPLAY_NAME%" ^
   --Startup=auto ^
   --StartMode=Jvm ^
   --Jvm="%JVM%" ^
   --StartPath="%PR_PATH%" ^
   --StartClass=%START_CLASS% ^
   --StartMethod=%START_METHOD% ^
   --LogPath="%LOG_PATH%" ^
   --PidFile=%PID_FILE% ^
   --Classpath="%PR_PATH%\%PR_JAR%" ^
   ++JvmOptions=%JVM_OPTIONS% ^
   --JvmMs=128 ^
   --JvmMx=256 ^
   --JvmSs=256 ^
   --LogPrefix=es-curator ^
   --StopMode=Jvm ^
   --StopClass=%STOP_CLASS% ^
   --StopMethod=%STOP_METHOD% ^
   ++StopParams="%STOP_PARAMS% ^
   --StopTimeout=30
goto:eof

:err
echo JAVA_HOME environment variable must be set!
pause
goto:eof

ENDLOCAL