# Acceptance Specification

This document outlines the acceptance criteria for the technical interview task based on the requirements provided in `README.md`.

## 1. Project Setup & Version Control

- [x] **Fork & Branch**: The project is forked to the candidate's GitHub account.
- [x] **Feature Branch**: A new feature branch (e.g., `feature/interview-implementation`) is created and used for all changes.
- [x] **Pull Request**: A Pull Request (PR) is created targeting the `main` branch upon completion.

## 2. Frontend: Internationalization (i18n)

### Requirement
Implement Chinese/English language switching for the Home page.

### Acceptance Criteria
- [x] **Dependencies**: `vue-i18n` is installed in `web/qa-web`.
- [x] **Locale Files**:
    - [x] `web/qa-web/src/locales/zh.json` exists and contains Chinese translations.
    - [x] `web/qa-web/src/locales/en.json` exists and contains English translations.
- [x] **UI Controls**: A language switcher (dropdown or button) is visible on the top right of the Home page (or Header).
- [x] **Functionality**:
    - [x] Default language is displayed correctly.
    - [x] Selecting "English" switches page content (Title, Subtitle, Features, Statistics, Active Rooms) to English.
    - [x] Selecting "中文" switches page content back to Chinese.
- [x] **Scope**: Changes are applied to the Home page (and Header/Footer where applicable). Other pages are optional but nice to have.

## 3. Backend: Database & API

### Requirement
Migrate doctor data from JSON to MySQL and expose via API.

### Acceptance Criteria
- [x] **Infrastructure**:
    - [x] `docker-compose.yml` is created in the project root.
    - [x] MySQL (v8.0) service is defined.
    - [x] phpMyAdmin service is defined and accessible (e.g., port 8085).
    - [x] *Note: In environments where Docker is unavailable, an H2 in-memory database with valid JPA configuration is an acceptable fallback for demonstration.*
- [x] **Data Model**: `Doctor` entity is created in `qa-service-user` matching the original JSON structure.
- [x] **API Implementation**:
    - [x] `GET /api/doctors` endpoint returns the list of all doctors.
    - [x] `GET /api/doctors/active` endpoint returns only active doctors (optional but recommended for Home page).
- [x] **Data Migration**: Initial data from `doctor-user-list.json` is loaded into the database on application startup.

## 4. Frontend-Backend Integration

### Requirement
The frontend should fetch data from the backend API instead of local JSON.

### Acceptance Criteria
- [x] **Data Source**: `web/qa-web/src/store/index.ts` (or relevant component) calls the backend API.
- [x] **Doctors Page**: The `/doctors` page displays the list of doctors fetched from the API.
- [x] **Home Page**: The "Active Rooms" section on the Home page displays active doctors fetched from the API.

## 5. Testing & Verification

### Requirement
Provide scripts to verify the API.

### Acceptance Criteria
- [x] **Test Scripts**: `API_TEST.md` is present in the root directory.
- [x] **Content**: The file contains `curl` commands to test:
    - [x] Fetching the doctor list.
    - [x] Any other created endpoints.
- [x] **Execution**: Running the `curl` commands against the running backend returns valid JSON data.

## 6. Documentation

### Requirement
Document the API.

### Acceptance Criteria
- [x] **File**: `/docs/api.md` is created.
- [x] **Content**:
    - [x] Describes the `qa-service-user` API endpoints.
    - [x] Includes request methods, URLs, and sample responses.
