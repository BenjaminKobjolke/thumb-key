@echo off
if not exist "%~dp0analyze_code_config.bat" (
    echo ERROR: analyze_code_config.bat not found.
    echo Copy analyzer settings into analyze_code_config.bat before running.
    exit /b 1
)
call "%~dp0analyze_code_config.bat"
cd /d "%~dp0.."

"%CLI_ANALYZER_PATH%\venv\Scripts\python.exe" "%CLI_ANALYZER_PATH%\main.py" --language %LANGUAGE% --path "." --verbosity minimal --output "code_analysis_results" --maxamountoferrors 50 --rules "code_analysis_rules.json"
set RC=%ERRORLEVEL%

cd /d "%~dp0"
exit /b %RC%
