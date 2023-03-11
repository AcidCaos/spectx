@echo off
title SpectX v1.4.83 Patch

:patch

echo SpectX v1.4.83 Patch & echo.

REM ######### Check Needed files #########

IF NOT EXIST spectx.jar (
	echo [!] Error: Expected spectx.jar in the current directory
	pause>NUL & exit )

IF NOT EXIST com\spectx\Ak.class (
	echo [!] Error: Expected com\spectx\Ak.class
	pause>NUL & exit )

REM ######### Backup patched JAR #########

IF NOT EXIST spectx.jar.bck (
	echo [+] Backup spectx.jar
	copy spectx.jar spectx.jar.bck
	IF %ERRORLEVEL% NEQ 0 echo [!] Error: Could not backup spectx.jar & pause>NUL & exit
	IF NOT EXIST spectx.jar.bck echo [!] Error: Could not backup spectx.jar & pause>NUL & exit )

REM ######### Delete JAR Verification #########

zip -d spectx.jar META-INF\CODESIGN.SF
zip -d spectx.jar META-INF\CODESIGN.RSA

REM ######### Patch Ak Class #########

echo [+] Patch Ak class
jar uf spectx.jar com\spectx\Ak.class

echo [+] Done!
pause>NUL