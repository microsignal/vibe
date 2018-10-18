@echo off
setlocal enabledelayedexpansion

rem "set APP_HOME"
if "%APP_HOME%" == "" (
	if exist %~dp0/%~nx0 (
		pushd %~dp0/..
		set APP_HOME=!cd!
		popd
	) else (
		set APP_HOME=!cd!
	)
)
rem echo "APP_HOME: %APP_HOME%"

:: set classpath
set SYS_CLASSPATH=%CLASSPATH%
set CLASSPATH=%APP_HOME%\conf;%APP_HOME%\bin

:: add main jars
for %%J in (%APP_HOME%\*.jar) do (
	rem echo %%~fJ
	set CLASSPATH=!CLASSPATH!;%%~fJ
)

:: add all dependencies
for %%J in (%APP_HOME%\lib\*.jar) do (
	REM echo %%~fJ
	SET CLASSPATH=!CLASSPATH!;%%~fJ
)

:: add system classpath
set CLASSPATH=!CLASSPATH!;%SYS_CLASSPATH%
set CLASSPATH=%CLASSPATH%

echo "CLASSPATH: %CLASSPATH%"
echo "JAVA_HOME: %JAVA_HOME%"

:: set java args
set JAVA_ARGS=
set JAVA_ARGS=%JAVA_ARGS% -Xms256m -Xmx1024m
set JAVA_ARGS=%JAVA_ARGS% -Dfile.encoding=UTF-8

set JAVA_ARGS=%JAVA_ARGS% -cp %CLASSPATH%
set JAVA_ARGS=%JAVA_ARGS% io.github.microvibe.util.tools.PackageChangerRunner

:: run java program
:: "%JAVA_HOME%\bin\java" %JAVA_ARGS% %APP_HOME%/conf/change.xml
"%JAVA_HOME%\bin\java" %JAVA_ARGS% %*

endlocal
:: pause
