# QA Live Healthcare - API curl test script (PowerShell)
# Usage: powershell -ExecutionPolicy Bypass -File docs/run-api-tests.ps1

param(
    [string]$BaseUrl = $(if ($env:BASE_URL) { $env:BASE_URL } else { "http://localhost:8080" })
)

$RegisterUser = "test-patient-$([DateTimeOffset]::UtcNow.ToUnixTimeSeconds())"
$Pass = 0
$Fail = 0

function Write-Header([string]$Title) {
    Write-Host ""
    Write-Host "========================================"
    Write-Host $Title
    Write-Host "========================================"
}

function Invoke-ApiTest {
    param(
        [string]$Id,
        [string]$Name,
        [int]$ExpectedCode,
        [string]$Method = "GET",
        [string]$Url,
        [string]$Body = $null
    )

    $tempFile = $null
    $curlArgs = @("-s", "-w", "`nHTTP_STATUS:%{http_code}", "-X", $Method, $Url)
    if ($Body) {
        $tempFile = [System.IO.Path]::GetTempFileName()
        [System.IO.File]::WriteAllText($tempFile, $Body, [System.Text.UTF8Encoding]::new($false))
        $curlArgs += @("-H", "Content-Type: application/json", "--data-binary", "@$tempFile")
    }

    try {
        $output = & curl.exe @curlArgs 2>&1
    } finally {
        if ($tempFile) {
            Remove-Item $tempFile -ErrorAction SilentlyContinue
        }
    }
    $outputText = ($output | Out-String).Trim()
    $statusMatch = [regex]::Match($outputText, "HTTP_STATUS:(\d+)")
    $httpCode = if ($statusMatch.Success) { [int]$statusMatch.Groups[1].Value } else { 0 }
    $responseBody = $outputText -replace "HTTP_STATUS:\d+\s*$", ""

    if ($httpCode -eq $ExpectedCode) {
        $script:Pass++
        $result = "PASS"
    } else {
        $script:Fail++
        $result = "FAIL"
    }

    Write-Host "[$result] $Id - $Name (expected HTTP $ExpectedCode, got HTTP $httpCode)"
    if ($result -eq "FAIL") {
        $preview = if ($responseBody.Length -gt 300) { $responseBody.Substring(0, 300) + "..." } else { $responseBody }
        Write-Host "  Response: $preview"
    }
}

$registerBody = (@{
    username = $RegisterUser
    password = "123456"
    name     = "Test User"
    birthday = "1992-01-01"
    gender   = "M"
} | ConvertTo-Json -Compress)

Write-Header "QA Live Healthcare API Tests"
Write-Host "BASE_URL: $BaseUrl"
Write-Host "Register test user: $RegisterUser"

Invoke-ApiTest -Id "TC-01" -Name "Health check" -ExpectedCode 200 -Url "$BaseUrl/actuator/health"
Invoke-ApiTest -Id "TC-02" -Name "CORS GET" -ExpectedCode 200 -Url "$BaseUrl/api/test/cors"
Invoke-ApiTest -Id "TC-03" -Name "List all doctors" -ExpectedCode 200 -Url "$BaseUrl/api/doctors"
Invoke-ApiTest -Id "TC-04" -Name "List active doctors" -ExpectedCode 200 -Url "$BaseUrl/api/doctors?active=true"
Invoke-ApiTest -Id "TC-05" -Name "Get doctor by username" -ExpectedCode 200 -Url "$BaseUrl/api/doctors/username/dr-zhang-wei"
Invoke-ApiTest -Id "TC-06" -Name "Doctor not found" -ExpectedCode 404 -Url "$BaseUrl/api/doctors/username/not-exist-user"
Invoke-ApiTest -Id "TC-07" -Name "Doctor login success" -ExpectedCode 200 -Method "POST" -Url "$BaseUrl/api/doctors/login" -Body '{"username":"dr-zhang-wei","password":"123456"}'
Invoke-ApiTest -Id "TC-08" -Name "Doctor login wrong password" -ExpectedCode 401 -Method "POST" -Url "$BaseUrl/api/doctors/login" -Body '{"username":"dr-zhang-wei","password":"wrong-password"}'
Invoke-ApiTest -Id "TC-09" -Name "Doctor login unknown user" -ExpectedCode 401 -Method "POST" -Url "$BaseUrl/api/doctors/login" -Body '{"username":"unknown-user","password":"123456"}'
Invoke-ApiTest -Id "TC-10" -Name "CORS POST" -ExpectedCode 200 -Method "POST" -Url "$BaseUrl/api/test/cors" -Body '{"test":"hello"}'
Invoke-ApiTest -Id "TC-11" -Name "Patient login success" -ExpectedCode 200 -Method "POST" -Url "$BaseUrl/api/patients/login" -Body '{"username":"zhaoming","password":"123456"}'
Invoke-ApiTest -Id "TC-12" -Name "Patient login failure" -ExpectedCode 401 -Method "POST" -Url "$BaseUrl/api/patients/login" -Body '{"username":"zhaoming","password":"wrong-password"}'
Invoke-ApiTest -Id "TC-13" -Name "Patient register success" -ExpectedCode 200 -Method "POST" -Url "$BaseUrl/api/patients/register" -Body $registerBody
Invoke-ApiTest -Id "TC-14" -Name "Patient register duplicate" -ExpectedCode 400 -Method "POST" -Url "$BaseUrl/api/patients/register" -Body '{"username":"zhaoming","password":"123456","name":"Duplicate User"}'

Write-Header "Summary"
$Total = $Pass + $Fail
Write-Host "Total:  $Total"
Write-Host "Passed: $Pass"
Write-Host "Failed: $Fail"
if ($Total -gt 0) {
    Write-Host "Pass rate: $([math]::Floor($Pass * 100 / $Total))%"
}

if ($Fail -gt 0) {
    Write-Host ""
    Write-Host "Some tests failed. Ensure MySQL and qa-service-user are running."
    exit 1
}

Write-Host ""
Write-Host "All tests passed."
exit 0
