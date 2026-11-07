# PowerShell Run Script for Project 1 - DBA Management Tool
Write-Host "Compiling source files..." -ForegroundColor Cyan
$sources = Get-ChildItem -Path "src/com/dbs/dba/*.java" | Select-Object -ExpandProperty FullName
javac -encoding UTF-8 -cp "lib/*" -d bin $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Starting DBA Management Tool..." -ForegroundColor Green
java -cp "bin;lib/*" com.dbs.dba.DBAToolApp
