-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: health_ins_db
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(255) NOT NULL,
  `details` varchar(255) DEFAULT NULL,
  `timestamp` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk4alalwu62gj4tfbgfefll3tu` (`user_id`),
  CONSTRAINT `FKk4alalwu62gj4tfbgfefll3tu` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
INSERT INTO `audit_log` VALUES (1,'LOGIN','Admin login successful','2025-10-08 15:31:23.985703',1),(2,'CLAIM_UPDATE','Updated claim status','2025-10-08 16:31:23.985703',4),(3,'USER_CREATED','Created new user account','2025-10-08 17:01:23.985703',1);
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank_accounts`
--

DROP TABLE IF EXISTS `bank_accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bank_accounts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_holder_name` varchar(255) NOT NULL,
  `account_number` varchar(255) NOT NULL,
  `bank_name` varchar(255) NOT NULL,
  `branch` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr9gi1et82prjsig51uqxj2qm6` (`account_number`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_accounts`
--

LOCK TABLES `bank_accounts` WRITE;
/*!40000 ALTER TABLE `bank_accounts` DISABLE KEYS */;
INSERT INTO `bank_accounts` VALUES (3,'san','123456789','BOC','malabe','2025-10-14 16:26:20.296074','2025-10-14 17:12:14.042212');
/*!40000 ALTER TABLE `bank_accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campaign`
--

DROP TABLE IF EXISTS `campaign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `campaign` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `end_date` date NOT NULL,
  `name` varchar(255) NOT NULL,
  `start_date` date NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','COMPLETED','DRAFT') NOT NULL,
  `target_segment` text,
  `type` enum('DISCOUNT','EMAIL','EMAIL_BLAST','PROMOTION','SMS','SOCIAL_MEDIA') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaign`
--

LOCK TABLES `campaign` WRITE;
/*!40000 ALTER TABLE `campaign` DISABLE KEYS */;
INSERT INTO `campaign` VALUES (1,NULL,'2025-11-07','Summer Health Check','2025-09-08','ACTIVE','Age 25-45, Active policies','EMAIL',NULL,NULL),(2,NULL,'2025-10-23','New Year Premium Discount','2025-09-23','ACTIVE','All customers','SMS',NULL,NULL),(3,NULL,'2025-11-14','Wellness Program Launch','2025-10-15','DRAFT','Health-conscious segment','SOCIAL_MEDIA',NULL,NULL),(4,NULL,'2025-10-23','one1','2025-10-11','DRAFT',NULL,'EMAIL',NULL,'dsa');
/*!40000 ALTER TABLE `campaign` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `claim`
--

DROP TABLE IF EXISTS `claim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `claim` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` double NOT NULL,
  `claim_date` date NOT NULL,
  `claim_id` varchar(100) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `document_path` varchar(500) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `status` enum('APPROVED','PAID','PENDING','REJECTED','UNDER_REVIEW') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `policy_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr3wubap6p20wbvebv40hdcrke` (`claim_id`),
  KEY `FKboylstrh3d1b9j39p2s7pdyym` (`policy_id`),
  CONSTRAINT `FKboylstrh3d1b9j39p2s7pdyym` FOREIGN KEY (`policy_id`) REFERENCES `policies` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `claim`
--

LOCK TABLES `claim` WRITE;
/*!40000 ALTER TABLE `claim` DISABLE KEYS */;
INSERT INTO `claim` VALUES (1,500,'2025-10-03','CLM-001',NULL,'/documents/claim1.pdf','Medical treatment for flu','REJECTED',NULL,1),(2,1200,'2025-09-28','CLM-002',NULL,'/documents/claim2.pdf','Emergency surgery','UNDER_REVIEW',NULL,2),(3,800,'2025-09-23','CLM-003',NULL,'/documents/claim3.pdf','Prescription medication','PAID',NULL,1),(4,895621,'2025-10-13','CLM-1760357469742',NULL,'N/A','qwertyui','REJECTED',NULL,1),(5,895621,'2025-10-13','CLM-1760357471498',NULL,'N/A','qwertyui','APPROVED',NULL,1),(6,0,'2025-10-13','CLM-1760357472648',NULL,'N/A','','PENDING',NULL,1),(10,10000,'2025-10-14','CLM-1760461417336',NULL,'N/A','ACCIDENT\nPayment processed successfully','PAID',NULL,9);
/*!40000 ALTER TABLE `claim` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_segment`
--

DROP TABLE IF EXISTS `customer_segment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_segment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `criteria` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_segment`
--

LOCK TABLES `customer_segment` WRITE;
/*!40000 ALTER TABLE `customer_segment` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_segment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_segment_customers`
--

DROP TABLE IF EXISTS `customer_segment_customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_segment_customers` (
  `segment_id` bigint NOT NULL,
  `customer_id` bigint DEFAULT NULL,
  KEY `FKhvhxbkq82j3870ns7es13qy8k` (`segment_id`),
  CONSTRAINT `FKhvhxbkq82j3870ns7es13qy8k` FOREIGN KEY (`segment_id`) REFERENCES `customer_segment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_segment_customers`
--

LOCK TABLES `customer_segment_customers` WRITE;
/*!40000 ALTER TABLE `customer_segment_customers` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_segment_customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `department` varchar(255) NOT NULL,
  `hire_date` date NOT NULL,
  `performance_rating` int DEFAULT NULL,
  `salary` double NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmpps3d3r9pdvyjx3iqixi96fi` (`user_id`),
  CONSTRAINT `FKhal2duyxxjtadykhxos7wd3wg` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,NULL,'IT','2023-10-08',5,75000,NULL,1),(2,NULL,'Customer Service','2024-04-08',4,45000,NULL,4),(3,NULL,'Claims Processing','2024-10-08',4,50000,NULL,5),(4,NULL,'Marketing','2025-02-08',5,60000,NULL,6),(5,NULL,'Human Resources','2025-04-08',4,55000,NULL,7),(6,NULL,'Customer','2025-10-14',3,0,NULL,19);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inquiries`
--

DROP TABLE IF EXISTS `inquiries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inquiries` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  `resolution_date` date DEFAULT NULL,
  `status` enum('OPEN','RESOLVED') NOT NULL,
  `title` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfks94q8sobcuibrudbr3im380` (`user_id`),
  CONSTRAINT `FKfks94q8sobcuibrudbr3im380` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inquiries`
--

LOCK TABLES `inquiries` WRITE;
/*!40000 ALTER TABLE `inquiries` DISABLE KEYS */;
INSERT INTO `inquiries` VALUES (1,NULL,'Question about premium payment','2025-10-14','RESOLVED','Premium Payment Issue','Billing',NULL,2),(2,NULL,'What is covered under my policy?','2025-10-06','RESOLVED','Coverage Question','Coverage',NULL,3),(3,NULL,'Status of my claim submission',NULL,'OPEN','Claim Status Inquiry','Claims',NULL,2),(4,NULL,'ygfbgsakjdhnka',NULL,'OPEN','dasdasda','BILLING',NULL,2);
/*!40000 ALTER TABLE `inquiries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `due_date` date NOT NULL,
  `payment_date` date DEFAULT NULL,
  `status` enum('OVERDUE','PAID','PENDING','REFUNDED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `policy_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc1adu7j283w0wuiajfycspyeg` (`policy_id`),
  CONSTRAINT `FKc1adu7j283w0wuiajfycspyeg` FOREIGN KEY (`policy_id`) REFERENCES `policies` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,150.00,NULL,'2025-10-03','2025-10-03','PAID',NULL,1),(2,200.00,NULL,'2025-10-05','2025-10-05','PAID',NULL,2),(3,150.00,NULL,'2025-11-02',NULL,'PENDING',NULL,1),(4,200.00,NULL,'2025-11-04',NULL,'PENDING',NULL,2),(5,800.00,NULL,'2025-10-13','2025-10-13','PAID',NULL,1),(10,200000.00,NULL,'2025-10-14','2025-10-14','PAID',NULL,9),(11,200000.00,NULL,'2025-10-14','2025-10-14','PAID',NULL,10);
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_reminder`
--

DROP TABLE IF EXISTS `payment_reminder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_reminder` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `schedule_date` date NOT NULL,
  `sent` bit(1) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn25frfu5lmmh468kg03s8mitw` (`user_id`),
  CONSTRAINT `FKn25frfu5lmmh468kg03s8mitw` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_reminder`
--

LOCK TABLES `payment_reminder` WRITE;
/*!40000 ALTER TABLE `payment_reminder` DISABLE KEYS */;
INSERT INTO `payment_reminder` VALUES (1,NULL,'2025-10-13',_binary '\0',2),(2,NULL,'2025-10-15',_binary '\0',3),(3,NULL,'2025-10-18',_binary '\0',2);
/*!40000 ALTER TABLE `payment_reminder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payroll`
--

DROP TABLE IF EXISTS `payroll`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payroll` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `date` date NOT NULL,
  `status` enum('PAID','PENDING') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `employee_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5o7fr6cbvrkgud2unv0p5rqlm` (`employee_id`),
  CONSTRAINT `FK5o7fr6cbvrkgud2unv0p5rqlm` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payroll`
--

LOCK TABLES `payroll` WRITE;
/*!40000 ALTER TABLE `payroll` DISABLE KEYS */;
INSERT INTO `payroll` VALUES (1,6250.00,NULL,'2025-10-03','PAID',NULL,1),(2,3750.00,NULL,'2025-10-03','PAID',NULL,2),(3,4166.67,NULL,'2025-10-03','PAID',NULL,3),(4,5000.00,NULL,'2025-10-03','PAID',NULL,4),(5,4583.33,NULL,'2025-10-03','PAID',NULL,5),(6,1000000.00,NULL,'2025-10-10','PAID',NULL,1),(7,10000.00,NULL,'2025-10-15','PAID',NULL,4),(8,10000.00,NULL,'2025-10-13','PAID',NULL,2);
/*!40000 ALTER TABLE `payroll` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `performance_review`
--

DROP TABLE IF EXISTS `performance_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `performance_review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comments` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `employee_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9159yuocyhexftv11wmay20cg` (`employee_id`),
  CONSTRAINT `FK9159yuocyhexftv11wmay20cg` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `performance_review`
--

LOCK TABLES `performance_review` WRITE;
/*!40000 ALTER TABLE `performance_review` DISABLE KEYS */;
INSERT INTO `performance_review` VALUES (1,'Excellent performance in system administration',NULL,'2025-09-08',5,1),(2,'Good customer service skills',NULL,'2025-09-13',4,2),(3,'Efficient claims processing',NULL,'2025-09-18',4,3),(4,'Creative marketing campaigns',NULL,'2025-09-23',5,4),(5,'Effective HR management',NULL,'2025-09-28',4,5);
/*!40000 ALTER TABLE `performance_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `policies`
--

DROP TABLE IF EXISTS `policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `policies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coverage` text NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `end_date` date NOT NULL,
  `policy_number` varchar(255) NOT NULL,
  `premium_amount` decimal(10,2) NOT NULL,
  `start_date` date NOT NULL,
  `status` enum('ACTIVE','EXPIRED','PENDING','SUSPENDED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKoa74bk3bbln2o1hgik4b93rp9` (`policy_number`),
  KEY `FKah8s5yurk7145x8c18kv9um0l` (`user_id`),
  CONSTRAINT `FKah8s5yurk7145x8c18kv9um0l` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `policies`
--

LOCK TABLES `policies` WRITE;
/*!40000 ALTER TABLE `policies` DISABLE KEYS */;
INSERT INTO `policies` VALUES (1,'Comprehensive Health Coverage',NULL,'2026-04-08','POL-001',150.00,'2025-04-08','ACTIVE',NULL,2),(2,'Premium Health Coverage',NULL,'2026-07-08','POL-002',200.00,'2025-07-08','ACTIVE',NULL,3),(3,'Basic Health Coverage',NULL,'2026-10-08','POL-003',100.00,'2025-11-07','PENDING',NULL,2),(9,'Premium Health Coverage',NULL,'2026-10-14','POL-1760461397271',200000.00,'2025-10-14','ACTIVE',NULL,19),(10,'Premium Health Coverage',NULL,'2026-10-14','POL-1760461399048',200000.00,'2025-10-14','ACTIVE',NULL,19);
/*!40000 ALTER TABLE `policies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `policy_info`
--

DROP TABLE IF EXISTS `policy_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `policy_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `benefits` text NOT NULL,
  `coverage_limit` varchar(255) NOT NULL,
  `coverage_type` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKk8v635eca4ck13rs6v6qjj9ia` (`coverage_type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `policy_info`
--

LOCK TABLES `policy_info` WRITE;
/*!40000 ALTER TABLE `policy_info` DISABLE KEYS */;
INSERT INTO `policy_info` VALUES (1,'Outpatient consultations, Basic diagnostic tests, Generic medications, Emergency services, Preventive care','Up to ,000 per year','Basic Health Coverage','2025-10-14 00:35:11.000000','Essential health coverage for basic medical needs','2025-10-14 00:35:11.000000',50000.00),(2,'All Basic coverage benefits, Specialist consultations, Advanced diagnostic procedures, Brand-name medications, Hospitalization coverage, Surgery coverage, Maternity care','Up to ,000 per year','Comprehensive Health Coverage','2025-10-14 00:35:11.000000','Complete health coverage for all medical needs','2025-10-14 00:35:11.000000',100000.00),(3,'All Comprehensive coverage benefits, VIP hospital rooms, Alternative treatments, International coverage, Dental and vision care, Mental health services, Wellness programs, 24/7 concierge medical service','Up to ,000 per year','Premium Health Coverage','2025-10-14 00:35:11.000000','Premium coverage with extensive benefits and privileges','2025-10-14 00:35:11.000000',200000.00);
/*!40000 ALTER TABLE `policy_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_config`
--

DROP TABLE IF EXISTS `system_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_config` (
  `config_key` bigint NOT NULL AUTO_INCREMENT,
  `config_name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_config`
--

LOCK TABLES `system_config` WRITE;
/*!40000 ALTER TABLE `system_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contact` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `role` enum('ADMIN','CLAIMS_PROCESSING','CUSTOMER','CUSTOMER_SERVICE','CUSTOMER_SERVICE_OFFICER','HR','IT_MANAGER','MARKETING','POLICYHOLDER','SUPPORT','USER') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `bank_account_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `UKiysc6lompsglaytnt2htcw8u` (`bank_account_id`),
  CONSTRAINT `FK9dg9wpc5dj5ls4o80ku820rmj` FOREIGN KEY (`bank_account_id`) REFERENCES `bank_accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin@healthins.com',NULL,'admin@healthins.com',_binary '','System Administrator','$2a$10$QEvCoflez.yPXUHVufKxeeEB1/OL1N2P/IfHv1krGJnCMgk3TTlhW','+1234567890','ADMIN',NULL,'admin',NULL),(2,'john@gmail.com',NULL,'john@email.com',_binary '\0','John Doe','$2a$10$hkIUQrijx0tduZsO3K4jOeKOI0ag0ExXXtKJ1MCATl1FkgFdZ5NKO','+1234567891','POLICYHOLDER',NULL,'john_doe',NULL),(3,'jane@email.com',NULL,'jane@email.com',_binary '','Jane Smith','$2a$10$2AEa4xOF/NvU3hBsYTYMG.Hj5MoXwrgH7EwkE.Cy/F.Kuqyk5ZkEC','+1234567892','POLICYHOLDER',NULL,'jane_smith',NULL),(4,'support@healthins.com',NULL,'support@healthins.com',_binary '','Support Agent','$2a$10$bjJXGrMo4u/MfD0ORw.TtODRgArp9HyXVyOOhZkaOv9H.ZnaNoC1O','+1234567893','CUSTOMER_SERVICE',NULL,'support_agent',NULL),(5,'claims@healthins.com',NULL,'claims@healthins.com',_binary '','Claims Processor','$2a$10$q2PoSOfJbD2/MGagCMar/eXktRmGJy6RthL0srS/IKMv8n2n1TUNW','+1234567894','CLAIMS_PROCESSING',NULL,'claims_processor',NULL),(6,'marketing@healthins.com',NULL,'marketing@healthins.com',_binary '','Marketing Manager','$2a$10$muTHE8QFlheuI7VTz.3q1O.c/AdgCdVV5j6OmOcd2DOYCl3FPoDU.','+1234567895','MARKETING',NULL,'marketing_manager',NULL),(7,'hr@healthins.com',NULL,'hr@healthins.com',_binary '','HR Manager','$2a$10$yDPcqfPLB.jRFUsI8fhOAuHfbJdP85wImxmXmegYpTiqowmhZ0fAC','+1234567896','HR',NULL,'hr_manager',NULL),(19,'demo@mail.com',NULL,'demo@mail.com',_binary '','demo','$2a$10$SIQ/fvpaDMOtUNjbCCpITeSXdYSukDUpgSKpXEB/YJGoGeThqSQgK','123456789','CUSTOMER',NULL,'demo',3);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-14 23:12:59
