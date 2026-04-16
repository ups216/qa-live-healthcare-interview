CREATE TABLE IF NOT EXISTS doctors (
    id VARCHAR(64) PRIMARY KEY,
    username VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    name VARCHAR(128) NOT NULL,
    title VARCHAR(128) NOT NULL,
    department VARCHAR(128) NOT NULL,
    avatar VARCHAR(512) NOT NULL,
    experience VARCHAR(255) NOT NULL,
    specialties TEXT NOT NULL,
    active BIT NOT NULL
);

CREATE TABLE IF NOT EXISTS patient_users (
    id VARCHAR(64) PRIMARY KEY,
    username VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    display_name VARCHAR(128) NOT NULL,
    birthday DATE NULL,
    phone VARCHAR(64) NULL,
    gender VARCHAR(32) NULL
);
