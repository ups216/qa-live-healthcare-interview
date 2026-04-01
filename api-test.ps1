# Simple API Test Script for qa-service-user Doctor APIs
# PowerShell version using curl.exe

$BaseUrl = "http://localhost:8081/api/doctors"

Write-Host "Starting API Tests..."
Write-Host "Base URL: $BaseUrl"
Write-Host "Test Time: $(Get-Date)"
Write-Host "=================================================="

# Test 1: Get all doctors
Write-Host "Test 1: Get All Doctors"
Write-Host "URL: $BaseUrl"
curl.exe -s -X GET $BaseUrl
Write-Host ""

# Test 2: Get active doctors
$url = "$BaseUrl/active"
Write-Host "Test 2: Get Active Doctors"
Write-Host "URL: $url"
curl.exe -s -X GET $url
Write-Host ""

# Test 3: Get doctor by ID
$url = "$BaseUrl/doc001"
Write-Host "Test 3: Get Doctor by ID"
Write-Host "URL: $url"
curl.exe -s -X GET $url
Write-Host ""

# Test 4: Get doctors by department (using English department name to avoid encoding issues)
$url = "$BaseUrl/department/cardiology"
Write-Host "Test 4: Get Doctors by Department"
Write-Host "URL: $url"
curl.exe -s -X GET $url
Write-Host ""

# Test 5: Get doctors by specialty (using English specialty name to avoid encoding issues)
$url = "$BaseUrl/specialty/heart-disease"
Write-Host "Test 5: Get Doctors by Specialty"
Write-Host "URL: $url"
curl.exe -s -X GET $url
Write-Host ""

# Test 6: Search doctors (using English parameters to avoid encoding issues)
$url = "$BaseUrl/search?name=zhangwei&department=cardiology"
Write-Host "Test 6: Search Doctors"
Write-Host "URL: $url"
curl.exe -s -X GET $url
Write-Host ""

# Test 7: Get all departments
$url = "$BaseUrl/departments"
Write-Host "Test 7: Get All Departments"
Write-Host "URL: $url"
curl.exe -s -X GET $url
Write-Host ""

# Test 8: Get all specialties
$url = "$BaseUrl/specialties"
Write-Host "Test 8: Get All Specialties"
Write-Host "URL: $url"
curl.exe -s -X GET $url
Write-Host ""

# Test 9: Test non-existent doctor ID
$url = "$BaseUrl/nonexistent"
Write-Host "Test 9: Test Non-existent Doctor ID"
Write-Host "URL: $url"
curl.exe -s -X GET $url
Write-Host ""

# Test 10: Test invalid request
$url = "$BaseUrl/invalid"
Write-Host "Test 10: Test Invalid Request"
Write-Host "URL: $url"
curl.exe -s -X GET $url
Write-Host ""

Write-Host "=================================================="
Write-Host "API Tests Completed"
Write-Host "Test completed at: $(Get-Date)"