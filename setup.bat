@echo off
echo Setting up AgriStock & AgriTrack...

:: Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo Java is not installed. Please install Java 26+.
    exit /b 1
)
echo Java is installed.

:: Check PostgreSQL
where psql >nul 2>&1
if errorlevel 1 (
    echo PostgreSQL is not installed. Please install PostgreSQL 16+.
    exit /b 1
)
echo PostgreSQL is installed.

:: Build projects
echo Building projects with Maven...
cd server
call mvn clean install -q
cd ..
cd client
call mvn clean install -q
cd ..

echo.
echo Setup complete!
echo.
echo Next steps:
echo 1. Edit server\src\main\resources\hibernate.cfg.xml with your DB credentials
echo 2. (Optional) Edit EmailService.java with Brevo SMTP credentials
echo 3. Start server: java -cp server\target\AgriStockRWServer-1.0-SNAPSHOT.jar com.wastonix.server.RmiServerBootstrap
echo 4. Start client: java -cp client\target\AgriStockRWClient-1.0-SNAPSHOT.jar com.wastonix.client.auth.LoginFrame
echo 5. First admin login: wastonorganisation@gmail.com
echo.
echo Full documentation: https://github.com/Leon-christian-00/AgriStock-RW#readme
pause