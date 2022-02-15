@ECHO OFF
SETLOCAL


:BEGIN
CLS
COLOR 3F >nul 2>&1
SET MC_SYS32=%SYSTEMROOT%\SYSTEM32
REM Make batch directory the same as the directory it's being called from
REM For example, if "run as admin" the batch starting dir could be system32
CD "%~dp0" >nul 2>&1


:CHECKPYTHON
ECHO INFO: Checking Python installation...
ECHO.

REM If no Python is installed this line will catch it simply
python --version >nul 2>&1 
ECHO.
IF %ERRORLEVEL% EQU 0 (
	ECHO INFO: Found Python.
	GOTO PREPAREVENV
) ELSE (
    GOTO PYTHONERROR
)

:PREPAREVENV
ECHO preparing the virtual environment...
python -m pip install --upgrade pip
python -m pip install --upgrade virtualenv
virtualenv --python python temp\venv
CALL "%cd%\temp\venv\Scripts\activate"
pip install bandit pyinstaller
GOTO MAIN


:MAIN

ECHO downloading latest bandit archive...
%SYSTEMROOT%\SYSTEM32\bitsadmin.exe /rawreturn /nowrap /transfer starter /dynamic /download /priority foreground https://github.com/PyCQA/bandit/archive/refs/heads/master.zip  "%cd%\temp\master.zip"

pushd "%cd%\temp"
ECHO unpacking bandit archive...
tar -xf "%cd%\master.zip"

ECHO creating bandit.exe...
pyinstaller --noconfirm --onefile --hidden-import pep517 --collect-all bandit "%cd%\bandit-master\bandit\__main__.py"
popd

move "%cd%\temp\dist\__main__.exe" "%cd%\bandit.exe"

CALL "%cd%\temp\venv\Scripts\deactivate"
ECHO deleting temporary files...
rmdir /s /q "%cd%\temp"
GOTO EOF

:PYTHONERROR
COLOR CF
ECHO ERROR: Could not find Python installed or in PATH
PAUSE


:EOF
PAUSE