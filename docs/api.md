# QA Service User API Documentation

## Overview

The `qa-service-user` microservice manages user data, including doctor profiles.

## Endpoints

### 1. Get All Doctors

Retrieves a list of all registered doctors.

- **URL**: `/api/doctors`
- **Method**: `GET`
- **Response**: `200 OK`
- **Body**: Array of `Doctor` objects

#### Response Example

```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "isActive": true,
    ...
  }
]
```

### 2. Get Active Doctors

Retrieves a list of doctors who are currently active and available for consultation.

- **URL**: `/api/doctors/active`
- **Method**: `GET`
- **Response**: `200 OK`
- **Body**: Array of `Doctor` objects

#### Response Example

```json
[
  {
    "id": "doc001",
    "username": "dr-zhang-wei",
    "name": "张伟医生",
    "title": "主任医师",
    "department": "心内科",
    "isActive": true,
    ...
  }
]
```

## Data Model

### Doctor

| Field | Type | Description |
|---|---|---|
| `id` | String | Unique identifier |
| `username` | String | Login username |
| `name` | String | Display name |
| `title` | String | Professional title (e.g., Chief Physician) |
| `department` | String | Medical department |
| `avatar` | String | URL to avatar image |
| `experience` | String | Years of experience |
| `specialties` | List<String> | List of medical specialties |
| `isActive` | Boolean | Availability status |
