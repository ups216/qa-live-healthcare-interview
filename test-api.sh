#!/bin/bash

# API Test Script for qa-service-user
# This script tests the doctors API endpoints using curl

set -e

BASE_URL="http://localhost:8080/api"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=========================================="
echo "QA Service User - API Test Script"
echo "=========================================="
echo ""

# Function to print success message
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error message
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Function to print info message
print_info() {
    echo -e "${YELLOW}→ $1${NC}"
}

# Test 1: Get all doctors
print_info "Test 1: GET /api/doctors - Get all doctors"
echo "Request: GET ${BASE_URL}/doctors"
RESPONSE=$(curl -s -w "\n%{http_code}" "${BASE_URL}/doctors")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    print_success "HTTP Status: $HTTP_CODE"
    DOCTOR_COUNT=$(echo "$BODY" | grep -o '"id"' | wc -l)
    print_success "Response: Found $DOCTOR_COUNT doctor(s)"
    echo "Response Body:"
    echo "$BODY"
else
    print_error "HTTP Status: $HTTP_CODE"
    echo "Response: $BODY"
fi
echo ""
echo "------------------------------------------"
echo ""

# Test 2: Get active doctors
print_info "Test 2: GET /api/doctors/active - Get active doctors"
echo "Request: GET ${BASE_URL}/doctors/active"
RESPONSE=$(curl -s -w "\n%{http_code}" "${BASE_URL}/doctors/active")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
    print_success "HTTP Status: $HTTP_CODE"
    ACTIVE_COUNT=$(echo "$BODY" | grep -o '"isActive":true' | wc -l)
    print_success "Response: Found $ACTIVE_COUNT active doctor(s)"
    echo "Response Body:"
    echo "$BODY"
else
    print_error "HTTP Status: $HTTP_CODE"
    echo "Response: $BODY"
fi
echo ""
echo "------------------------------------------"
echo ""

# Test 3: Verify CORS headers
print_info "Test 3: Verify CORS headers"
echo "Request: OPTIONS ${BASE_URL}/doctors"
RESPONSE=$(curl -s -I -X OPTIONS \
    -H "Origin: http://localhost:5174" \
    -H "Access-Control-Request-Method: GET" \
    "${BASE_URL}/doctors")

CORS_HEADER=$(echo "$RESPONSE" | grep -i "Access-Control-Allow-Origin" || true)
if [ -n "$CORS_HEADER" ]; then
    print_success "CORS header present: $CORS_HEADER"
else
    print_error "CORS header missing"
fi
echo ""
echo "------------------------------------------"
echo ""

# Test 4: Response structure validation
print_info "Test 4: Validate response structure"
RESPONSE=$(curl -s "${BASE_URL}/doctors")
REQUIRED_FIELDS='"id" "username" "name" "title" "department" "avatar" "experience" "specialties" "isActive"'
ALL_FIELDS_PRESENT=true

for FIELD in $REQUIRED_FIELDS; do
    if echo "$RESPONSE" | grep -q "$FIELD"; then
        continue
    else
        print_error "Missing field: $FIELD"
        ALL_FIELDS_PRESENT=false
    fi
done

if [ "$ALL_FIELDS_PRESENT" = true ]; then
    print_success "All required fields present in response"
fi
echo ""
echo "=========================================="
echo "API Test Completed"
echo "=========================================="
