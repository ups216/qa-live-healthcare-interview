# 医生列表 API 测试脚本
$BASE_URL = "http://localhost:8080/api/doctors"
$LOG_FILE = "test-api.log"

# 清空日志文件
"========================================" | Out-File -FilePath $LOG_FILE -Encoding UTF8
"医生列表 API 测试脚本 - $(Get-Date)" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
"========================================" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
"" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8

function Test-API {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [hashtable]$Body = $null
    )
    
    Write-Host ""
    Write-Host "[$Name]" -ForegroundColor Cyan
    Write-Host "Method: $Method"
    Write-Host "URL: $Url"
    Write-Host "--------------------------------------------"
    
    # 写入日志
    "" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
    "[$Name]" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
    "Method: $Method" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
    "URL: $Url" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
    
    try {
        $params = @{
            Uri = $Url
            Method = $Method
            ContentType = "application/json"
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json)
        }
        
        $response = Invoke-RestMethod @params
        
        Write-Host "Status: SUCCESS (Code: $($response.code))" -ForegroundColor Green
        Write-Host "Response preview:" 
        $response.data | Select-Object -First 2 | Format-Table -AutoSize | Out-String | Write-Host
        
        # 写入日志
        "Status: SUCCESS (Code: $($response.code))" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
        "Response: $($response | ConvertTo-Json -Depth 3)" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
        
        return $true
    }
    catch {
        Write-Host "Status: FAILED" -ForegroundColor Red
        Write-Host "Error: $_"
        
        # 写入日志
        "Status: FAILED" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
        "Error: $_" | Out-File -FilePath $LOG_FILE -Append -Encoding UTF8
        
        return $false
    }
}

Write-Host "========================================" -ForegroundColor Yellow
Write-Host "医生列表 API 测试脚本" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Yellow

# 测试用例
$tests = @(
    @{ Name = "测试1-获取所有医生(中文)"; Method = "GET"; Url = "$BASE_URL`?lang=zh" },
    @{ Name = "测试2-获取所有医生(英文)"; Method = "GET"; Url = "$BASE_URL`?lang=en" },
    @{ Name = "测试3-获取在线医生(中文)"; Method = "GET"; Url = "$BASE_URL/active`?lang=zh" },
    @{ Name = "测试4-获取在线医生(英文)"; Method = "GET"; Url = "$BASE_URL/active`?lang=en" },
    @{ Name = "测试5-获取医生详情"; Method = "GET"; Url = "$BASE_URL/doc001`?lang=zh" },
    @{ Name = "测试6-医生登录成功"; Method = "POST"; Url = "$BASE_URL/login`?lang=zh"; Body = @{ username = "dr-zhang-wei"; password = "123456" } },
    @{ Name = "测试7-医生登录失败"; Method = "POST"; Url = "$BASE_URL/login`?lang=zh"; Body = @{ username = "dr-zhang-wei"; password = "wrong" } }
)

$passed = 0
$failed = 0

foreach ($test in $tests) {
    $result = Test-API -Name $test.Name -Method $test.Method -Url $test.Url -Body $test.Body
    if ($result) {
        $passed++
    } else {
        $failed++
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Yellow
Write-Host "测试完成!" -ForegroundColor Yellow
Write-Host "通过: $passed, 失败: $failed" -ForegroundColor $(if ($failed -eq 0) { "Green" } else { "Red" })
Write-Host "详细日志: $LOG_FILE" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Yellow
