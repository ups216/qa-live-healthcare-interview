@echo off
echo Starting Backend Spring Boot Service...
cd server\qa-service-user
"D:\program\apache-maven-3.8.1\bin\mvn.cmd" spring-boot:run
pause
