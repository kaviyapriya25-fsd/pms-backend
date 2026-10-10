-- ==============================================================================
-- PMS ENTERPRISE REALISTIC SEED DATA SCRIPT - EXPANDED & PRODUCTION-READY
-- Database: project_management
-- Company: TechNova Solutions Pvt Ltd (ID: 1)
-- ==============================================================================

USE project_management;

-- 1. ADD / UPDATE TEAM LEADERS
-- Leader 3: Rajalakshmi S (AI Healthcare Diagnostic Portal)
INSERT INTO team_leaders (name, email, department, phone, project, status, joining_date, password, role, company_id, created_at)
SELECT 'Rajalakshmi S', 'rajalakshmi.lead@technova.com', 'Healthcare Informatics', '9840198765', 'AI Healthcare Diagnostic Portal', 'ACTIVE', '2026-06-01', 'Itachi007', 'TEAM_LEADER', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM team_leaders WHERE email = 'rajalakshmi.lead@technova.com');

INSERT INTO users (name, email, password, role, company_id, status, phone, department, project, designation, created_at)
SELECT 'Rajalakshmi S', 'rajalakshmi.lead@technova.com', 'Itachi007', 'TEAM_LEADER', 1, 'ACTIVE', '9840198765', 'Healthcare Informatics', 'AI Healthcare Diagnostic Portal', 'Principal Engineering Lead', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'rajalakshmi.lead@technova.com');

-- Leader 4: Arun Kumar (Smart Fleet & Logistics ERP)
INSERT INTO team_leaders (name, email, department, phone, project, status, joining_date, password, role, company_id, created_at)
SELECT 'Arun Kumar', 'arun.lead@technova.com', 'IoT & Cloud Systems', '9894567890', 'Smart Fleet & Logistics ERP', 'ACTIVE', '2026-06-15', 'Itachi007', 'TEAM_LEADER', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM team_leaders WHERE email = 'arun.lead@technova.com');

INSERT INTO users (name, email, password, role, company_id, status, phone, department, project, designation, created_at)
SELECT 'Arun Kumar', 'arun.lead@technova.com', 'Itachi007', 'TEAM_LEADER', 1, 'ACTIVE', '9894567890', 'IoT & Cloud Systems', 'Smart Fleet & Logistics ERP', 'IoT Solutions Architect', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'arun.lead@technova.com');


-- 2. ADD REALISTIC EMPLOYEES
-- Pooja Dharshini (under Rajalakshmi S)
INSERT INTO employees (name, email, designation, phone, project, team_leader, status, joining_date, password, role, company_id, created_at)
SELECT 'Pooja Dharshini', 'pooja.ml@technova.com', 'Machine Learning & CV Engineer', '9789123411', 'AI Healthcare Diagnostic Portal', 'Rajalakshmi S', 'ACTIVE', '2026-07-01', 'Itachi007', 'EMPLOYEE', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE email = 'pooja.ml@technova.com');

INSERT INTO users (name, email, password, role, company_id, status, phone, department, project, designation, created_at)
SELECT 'Pooja Dharshini', 'pooja.ml@technova.com', 'Itachi007', 'EMPLOYEE', 1, 'ACTIVE', '9789123411', 'Healthcare Informatics', 'AI Healthcare Diagnostic Portal', 'Machine Learning & CV Engineer', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'pooja.ml@technova.com');

-- Dinesh Karthik (under Rajalakshmi S)
INSERT INTO employees (name, email, designation, phone, project, team_leader, status, joining_date, password, role, company_id, created_at)
SELECT 'Dinesh Karthik', 'dinesh.bio@technova.com', 'Biomedical Data Engineer', '9894789012', 'AI Healthcare Diagnostic Portal', 'Rajalakshmi S', 'ACTIVE', '2026-07-15', 'Itachi007', 'EMPLOYEE', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE email = 'dinesh.bio@technova.com');

INSERT INTO users (name, email, password, role, company_id, status, phone, department, project, designation, created_at)
SELECT 'Dinesh Karthik', 'dinesh.bio@technova.com', 'Itachi007', 'EMPLOYEE', 1, 'ACTIVE', '9894789012', 'Healthcare Informatics', 'AI Healthcare Diagnostic Portal', 'Biomedical Data Engineer', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'dinesh.bio@technova.com');

-- Naveen Prakash (under Arun Kumar)
INSERT INTO employees (name, email, designation, phone, project, team_leader, status, joining_date, password, role, company_id, created_at)
SELECT 'Naveen Prakash', 'naveen.iot@technova.com', 'Embedded Firmware & IoT Engineer', '9443567123', 'Smart Fleet & Logistics ERP', 'Arun Kumar', 'ACTIVE', '2026-06-25', 'Itachi007', 'EMPLOYEE', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE email = 'naveen.iot@technova.com');

INSERT INTO users (name, email, password, role, company_id, status, phone, department, project, designation, created_at)
SELECT 'Naveen Prakash', 'naveen.iot@technova.com', 'Itachi007', 'EMPLOYEE', 1, 'ACTIVE', '9443567123', 'IoT & Cloud Systems', 'Smart Fleet & Logistics ERP', 'Embedded Firmware & IoT Engineer', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'naveen.iot@technova.com');

-- Swetha Ramesh (under Arun Kumar)
INSERT INTO employees (name, email, designation, phone, project, team_leader, status, joining_date, password, role, company_id, created_at)
SELECT 'Swetha Ramesh', 'swetha.fe@technova.com', 'GIS Mapping & React Developer', '9629012399', 'Smart Fleet & Logistics ERP', 'Arun Kumar', 'ACTIVE', '2026-07-05', 'Itachi007', 'EMPLOYEE', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE email = 'swetha.fe@technova.com');

INSERT INTO users (name, email, password, role, company_id, status, phone, department, project, designation, created_at)
SELECT 'Swetha Ramesh', 'swetha.fe@technova.com', 'Itachi007', 'EMPLOYEE', 1, 'ACTIVE', '9629012399', 'IoT & Cloud Systems', 'Smart Fleet & Logistics ERP', 'GIS Mapping & React Developer', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'swetha.fe@technova.com');

-- Divya Bharathi (under Kaviya)
INSERT INTO employees (name, email, designation, phone, project, team_leader, status, joining_date, password, role, company_id, created_at)
SELECT 'Divya Bharathi', 'divya.dev@technova.com', 'Backend Microservices Specialist', '9840223344', 'Placement Management System', 'Kaviya', 'ACTIVE', '2026-08-01', 'Itachi007', 'EMPLOYEE', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE email = 'divya.dev@technova.com');

INSERT INTO users (name, email, password, role, company_id, status, phone, department, project, designation, created_at)
SELECT 'Divya Bharathi', 'divya.dev@technova.com', 'Itachi007', 'EMPLOYEE', 1, 'ACTIVE', '9840223344', 'Engineering', 'Placement Management System', 'Backend Microservices Specialist', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'divya.dev@technova.com');


-- 3. LINK PROJECTS TO APPROPRIATE TEAM LEADERS
UPDATE projects 
SET 
  leader = 'Rajalakshmi S',
  priority = 'HIGH',
  status = 'IN_PROGRESS',
  start_date = '2026-08-15',
  end_date = '2026-11-30',
  description = 'HIPAA-compliant medical imaging pipeline with automated chest X-ray and CT anomaly segmentation using convolutional deep learning networks.'
WHERE id = 4 OR project_name = 'AI Healthcare Diagnostic Portal';

UPDATE projects 
SET 
  leader = 'Arun Kumar',
  priority = 'MEDIUM',
  status = 'IN_PROGRESS',
  start_date = '2026-07-20',
  end_date = '2026-12-15',
  description = 'Real-time telemetry and supply chain ERP processing CAN-bus telemetry, automated geo-fence triggers, and cold-chain temperature thresholds.'
WHERE id = 5 OR project_name = 'Smart Fleet & Logistics ERP';

-- Add 6th Project: NextGen Omnichannel E-Commerce Hub
INSERT INTO projects (project_name, description, start_date, end_date, status, priority, leader, company_id, created_at)
SELECT 
  'NextGen Omnichannel E-Commerce Hub',
  'High-throughput microservices commerce platform featuring dynamic inventory lock, multi-currency payment checkout, and Algolia instant faceted search.',
  '2026-09-01',
  '2026-12-31',
  'ACTIVE',
  'HIGH',
  'Kaviya',
  1,
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE project_name = 'NextGen Omnichannel E-Commerce Hub');


-- 4. INSERT ENTERPRISE SPRINT TASKS

-- Tasks for: AI Healthcare Diagnostic Portal (Leader: Rajalakshmi S)
INSERT INTO tasks (task_name, description, project, employee, team_leader, priority, status, start_date, due_date, company_id, estimated_hours, logged_hours, sprint_name, story_points, work_submission, created_at)
VALUES
(
  'DICOM Medical Imaging Ingestion & Parsing Microservice',
  'Implement asynchronous DICOM metadata parser with lossless decompression and AWS S3 encrypted bucket archiving.',
  'AI Healthcare Diagnostic Portal',
  'Pooja Dharshini',
  'Rajalakshmi S',
  'HIGH',
  'COMPLETED',
  '2026-08-16',
  '2026-08-28',
  1,
  24.0,
  22.5,
  'Sprint 1 - Core Ingestion',
  5,
  'https://github.com/technova-health/dicom-parser/pull/12',
  NOW()
),
(
  'HIPAA-Compliant Patient Data Anonymization & Encryption at Rest',
  'Zero-knowledge anonymizer masking patient name, age, and location prior to model inference using AES-256 GCM encryption.',
  'AI Healthcare Diagnostic Portal',
  'Dinesh Karthik',
  'Rajalakshmi S',
  'HIGH',
  'COMPLETED',
  '2026-08-25',
  '2026-09-05',
  1,
  20.0,
  19.0,
  'Sprint 1 - Core Ingestion',
  5,
  'https://github.com/technova-health/hipaa-guard/commit/8f2c31',
  NOW()
),
(
  'Deep Learning Chest X-Ray Anomaly Detection Inference API',
  'Serve PyTorch ResNet-50 model via ONNX Runtime with sub-120ms inference latency and heat-map overlay generation.',
  'AI Healthcare Diagnostic Portal',
  'Pooja Dharshini',
  'Rajalakshmi S',
  'URGENT',
  'IN_PROGRESS',
  '2026-09-06',
  '2026-10-25',
  1,
  35.0,
  18.0,
  'Sprint 2 - AI Diagnostic Inference',
  8,
  'https://github.com/technova-health/ai-inference/pull/4',
  NOW()
),
(
  'Interactive Radiologist Diagnostic Review & Annotation Studio',
  'Canvas-based medical viewer with zoom, pan, brightness windowing, and digital signature sign-off workflows.',
  'AI Healthcare Diagnostic Portal',
  'Dinesh Karthik',
  'Rajalakshmi S',
  'MEDIUM',
  'IN_REVIEW',
  '2026-09-15',
  '2026-10-22',
  1,
  28.0,
  24.0,
  'Sprint 2 - AI Diagnostic Inference',
  5,
  'https://www.figma.com/file/technova-radiology-review-studio',
  NOW()
),
(
  'Automated Clinical Lab Report PDF Generator with Barcode',
  'Generate multi-page PDF diagnosis summary incorporating QR code verification, hospital letterhead, and disclaimer footer.',
  'AI Healthcare Diagnostic Portal',
  'Dinesh Karthik',
  'Rajalakshmi S',
  'MEDIUM',
  'TODO',
  '2026-10-01',
  '2026-11-05',
  1,
  16.0,
  0.0,
  'Sprint 3 - Clinical Reporting',
  3,
  NULL,
  NOW()
);

-- Tasks for: Smart Fleet & Logistics ERP (Leader: Arun Kumar)
INSERT INTO tasks (task_name, description, project, employee, team_leader, priority, status, start_date, due_date, company_id, estimated_hours, logged_hours, sprint_name, story_points, work_submission, created_at)
VALUES
(
  'OBD-II CAN-Bus Telematics Ingestion over MQTT & Kafka',
  'High-throughput MQTT broker receiving vehicle speed, engine RPM, fuel consumption, and battery health telemetry at 10Hz frequency.',
  'Smart Fleet & Logistics ERP',
  'Naveen Prakash',
  'Arun Kumar',
  'HIGH',
  'COMPLETED',
  '2026-07-22',
  '2026-08-10',
  1,
  30.0,
  29.0,
  'Sprint 1 - Telemetry Protocol',
  8,
  'https://github.com/technova-logistics/fleet-telematics/pull/18',
  NOW()
),
(
  'Real-Time Leaflet & Mapbox Live Fleet Tracking Dashboard',
  'Interactive map with cluster markers, smooth vehicle vehicle interpolation, route breadcrumbs, and speed alerts.',
  'Smart Fleet & Logistics ERP',
  'Swetha Ramesh',
  'Arun Kumar',
  'HIGH',
  'IN_PROGRESS',
  '2026-08-12',
  '2026-10-24',
  1,
  32.0,
  20.0,
  'Sprint 2 - Live Geofencing & Tracking',
  8,
  'https://github.com/technova-logistics/fleet-ui/commit/39da12',
  NOW()
),
(
  'Dynamic Geofencing Engine & Unauthorized Deviation Alerter',
  'Ray-casting polygon point-in-polygon containment detection triggering push notifications when delivery vehicles exit designated transit corridors.',
  'Smart Fleet & Logistics ERP',
  'Naveen Prakash',
  'Arun Kumar',
  'URGENT',
  'IN_TESTING',
  '2026-08-28',
  '2026-10-20',
  1,
  25.0,
  22.0,
  'Sprint 2 - Live Geofencing & Tracking',
  5,
  'https://github.com/technova-logistics/fleet-geofence/pull/7',
  NOW()
),
(
  'Driver Fatigue & Continuous Hours-of-Service Rule Engine',
  'Automated ELD compliance tracking shift start, break intervals, and driving caps with automatic SMS alert escalations.',
  'Smart Fleet & Logistics ERP',
  'Swetha Ramesh',
  'Arun Kumar',
  'MEDIUM',
  'TODO',
  '2026-09-10',
  '2026-11-12',
  1,
  18.0,
  2.0,
  'Sprint 3 - Fleet Compliance',
  3,
  NULL,
  NOW()
),
(
  'Cold-Chain Temperature Sensor Anomaly Alerting Pipeline',
  'Monitor refrigeration unit BLE sensors for temperature breaches with real-time audio and webhook alerts.',
  'Smart Fleet & Logistics ERP',
  'Naveen Prakash',
  'Arun Kumar',
  'MEDIUM',
  'TODO',
  '2026-09-20',
  '2026-11-20',
  1,
  16.0,
  0.0,
  'Sprint 3 - Fleet Compliance',
  3,
  NULL,
  NOW()
);

-- Additional Tasks for: Placement Management System (Leader: Kaviya)
INSERT INTO tasks (task_name, description, project, employee, team_leader, priority, status, start_date, due_date, company_id, estimated_hours, logged_hours, sprint_name, story_points, work_submission, created_at)
VALUES
(
  'Placement Drive Automated WhatsApp & Email Announcement Dispatcher',
  'Queue-based bulk dispatch engine using Spring Boot Batch and MailGun API for immediate drive eligibility blasts to 1,200+ students.',
  'Placement Management System',
  'Divya Bharathi',
  'Kaviya',
  'HIGH',
  'COMPLETED',
  '2026-08-10',
  '2026-08-22',
  1,
  18.0,
  17.5,
  'Sprint 2 - Drive Execution',
  5,
  'https://github.com/technova-placement/drive-announcer/pull/8',
  NOW()
),
(
  'Offer Letter Digital Signature & Verification Portal',
  'Cryptographically signed offer acceptance workflow with cryptographic checksum, timestamping, and HR acknowledgement.',
  'Placement Management System',
  'Divya Bharathi',
  'Kaviya',
  'MEDIUM',
  'IN_PROGRESS',
  '2026-09-01',
  '2026-10-30',
  1,
  24.0,
  14.0,
  'Sprint 3 - Onboarding & Offers',
  5,
  'https://github.com/technova-placement/offer-verifier/commit/c2091a',
  NOW()
);

-- Additional Tasks for: NextGen Omnichannel E-Commerce Hub (Leader: Kaviya)
INSERT INTO tasks (task_name, description, project, employee, team_leader, priority, status, start_date, due_date, company_id, estimated_hours, logged_hours, sprint_name, story_points, work_submission, created_at)
VALUES
(
  'Stripe & Razorpay Multi-Currency Checkout Gateway Adapter',
  'Idempotent payment webhook processor with HMAC signature verification, currency conversion, and automated receipts.',
  'NextGen Omnichannel E-Commerce Hub',
  'Karthik Raja',
  'Kaviya',
  'HIGH',
  'IN_PROGRESS',
  '2026-09-05',
  '2026-10-28',
  1,
  26.0,
  16.0,
  'Sprint 1 - Checkout Core',
  5,
  'https://github.com/technova-commerce/checkout-service/pull/15',
  NOW()
),
(
  'Redis Distributed Lock for High-Concurrency Flash Sale Inventory',
  'Prevent negative stock allocations using Redisson distributed semaphore locking with 500ms timeout fallbacks.',
  'NextGen Omnichannel E-Commerce Hub',
  'Rajii',
  'Kaviya',
  'URGENT',
  'IN_PROGRESS',
  '2026-09-12',
  '2026-10-26',
  1,
  22.0,
  15.0,
  'Sprint 1 - Checkout Core',
  5,
  'https://github.com/technova-commerce/inventory-lock/commit/7a29e1',
  NOW()
),
(
  'Algolia Instant Faceted Catalog Search & Auto-Suggest Widget',
  'Typo-tolerant product discovery index with dynamic price facet sliders, brand checkboxes, and instant caching.',
  'NextGen Omnichannel E-Commerce Hub',
  'Abi',
  'Kaviya',
  'MEDIUM',
  'TODO',
  '2026-09-20',
  '2026-11-10',
  1,
  16.0,
  0.0,
  'Sprint 2 - Catalog Discovery',
  3,
  NULL,
  NOW()
);


-- 5. REALISTIC WORKLOGS
INSERT INTO worklogs (task_id, user_name, user_email, hours_spent, work_description, logged_at)
VALUES
(1, 'Deepa', '24ucs15@tcarts.in', 5.5, 'Completed webhook signature validation logic and handled timeout retries with exponential backoff.', NOW() - INTERVAL 4 DAY),
(2, 'Abi', '24ucs18@gmail.com', 6.0, 'Integrated Google OAuth 2.0 PKCE flow on React frontend and validated JWT claims against backend.', NOW() - INTERVAL 3 DAY),
(3, 'Rajii', 'raji@gmail.com', 4.5, 'Designed recruiter job creation schema and added GPA and backlog eligibility criteria filters.', NOW() - INTERVAL 2 DAY),
(4, 'Karthik Raja', 'karthik.dev@technova.com', 5.0, 'Trained Spacy NER model on 400 sample resumes to extract core technical competencies with 94% precision.', NOW() - INTERVAL 1 DAY),
(7, 'Vigneshwaran M', 'vignesh.dev@technova.com', 6.5, 'Configured Twilio Verify SDK for SMS 2FA and tested SIM-swap detection threshold.', NOW() - INTERVAL 2 DAY),
(8, 'Deepa', '24ucs15@tcarts.in', 4.0, 'Added indexed composite key on (account_number, transaction_date) reducing query latency from 850ms to 24ms.', NOW() - INTERVAL 1 DAY);


-- 6. SYSTEM-WIDE BROADCAST NOTIFICATIONS
INSERT INTO notification (title, message, type, time, is_read, company_id, recipient_role)
VALUES
('🚀 Q4 Enterprise Sprints Launched', 'All engineering teams: Sprints for AI Healthcare, Smart Fleet, and Commerce Hub are now active.', 'INFO', 'Just now', b'0', 1, 'ALL'),
('🔒 Security Audit Passed (ISO 27001)', 'TechNova Solutions completed the annual external security penetration test with zero high-severity findings.', 'SUCCESS', '1 day ago', b'0', 1, 'ALL'),
('⚡ High Load Preparedness Alert', 'Placement Management System expecting 1,200 concurrent student registrations this Thursday at 10:00 AM.', 'WARNING', '2 days ago', b'0', 1, 'ALL'),
('📊 October Executive Reports Ready', 'Monthly velocity charts, task burndown reports, and team hour logs are now available for export in PDF/Excel.', 'INFO', '3 days ago', b'1', 1, 'ADMIN');

