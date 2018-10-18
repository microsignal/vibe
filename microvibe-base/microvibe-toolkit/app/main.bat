@echo off
setlocal enabledelayedexpansion

rem "set APP_HOME"
if "%APP_HOME%" == "" (
	if exist %~dp0/%~nx0 (
		pushd %~dp0
		set APP_HOME=!cd!
		popd
	) else (
		set APP_HOME=!cd!
	)
)
echo "APP_HOME: %APP_HOME%"

%APP_HOME%\bin\change.bat %*
:: ping localhost -n 5 >nul
:: pause
