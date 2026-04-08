CREATE TABLE IF NOT EXISTS doctors (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  title VARCHAR(100),
  department VARCHAR(100),
  hospital VARCHAR(150),
  avatar VARCHAR(255),
  specialty VARCHAR(255)
);

INSERT INTO doctors (name, title, department, hospital, avatar, specialty) VALUES
('Dr. Sarah Chen', 'Chief Physician', 'Cardiology', 'City General Hospital', '/images/doctor1.jpg', 'Heart disease treatment'),
('Dr. Michael Lee', 'Attending Physician', 'Neurology', 'City General Hospital', '/images/doctor2.jpg', 'Stroke and headache'),
('Dr. Emily Wang', 'Associate Chief Physician', 'Pediatrics', 'Sunrise Medical Center', '/images/doctor3.jpg', 'Child health management'),
('Dr. David Zhang', 'Physician', 'Orthopedics', 'Sunrise Medical Center', '/images/doctor4.jpg', 'Bone and joint treatment'),
('Dr. Linda Liu', 'Chief Physician', 'Dermatology', 'Harmony Hospital', '/images/doctor5.jpg', 'Skin disease diagnosis');