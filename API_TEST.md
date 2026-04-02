# API Testing Instructions

This document provides `curl` commands to test the Doctor API endpoints.

## Base URL
`http://localhost:8080`

## 1. Get All Doctors

Retrieve a list of all doctors.

```bash
curl -v http://localhost:8080/api/doctors
```

**Expected Output:**
A JSON array containing doctor objects.

## 2. Get Active Doctors

Retrieve a list of currently active doctors.

```bash
curl -v http://localhost:8080/api/doctors/active
```

**Expected Output:**
A JSON array containing only doctors with `isActive: true`.
