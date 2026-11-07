@echo off
echo Compiling Java source files...
if not exist "bin" mkdir bin
javac -encoding UTF-8 -cp "lib/*" -d bin src/com/dbs/dba/*.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

echo Starting DBA Management Tool...
java -cp "bin;lib/*" com.dbs.dba.DBAToolApp
pause
