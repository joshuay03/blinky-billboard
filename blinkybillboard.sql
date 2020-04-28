-- MariaDB dump 10.17  Distrib 10.4.12-MariaDB, for Linux (x86_64)
--
-- Host: 192.168.0.172    Database: blinkyBillboard
-- ------------------------------------------------------
-- Server version	10.4.10-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Billboards`
--

DROP TABLE IF EXISTS `Billboards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Billboards` (
  `billboard_id` int(11) unsigned NOT NULL,
  `duration` int(11) unsigned NOT NULL COMMENT 'How long will one instance of this billboard be up for?',
  `creator` int(11) unsigned NOT NULL COMMENT 'User ID of the billboard''s creator',
  `XML` text COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'The billboard''s XML data - stored directly rather than as an external file',
  PRIMARY KEY (`billboard_id`),
  KEY `fk_creator_idx` (`creator`),
  CONSTRAINT `fk_creator` FOREIGN KEY (`creator`) REFERENCES `Users` (`user_id`) ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Billboards`
--

LOCK TABLES `Billboards` WRITE;
/*!40000 ALTER TABLE `Billboards` DISABLE KEYS */;
/*!40000 ALTER TABLE `Billboards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Scheduling`
--

DROP TABLE IF EXISTS `Scheduling`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Scheduling` (
  `schedule_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The ID is per billboard, per viewer',
  `billboard_id` int(11) unsigned NOT NULL,
  `viewer_id` int(11) unsigned NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `interval` int(11) unsigned NOT NULL COMMENT 'How often should the billboard repeat itself in minutes? (must be more than the duration )',
  `scheduled_at` time NOT NULL DEFAULT current_timestamp() COMMENT 'When was the billboard created?',
  PRIMARY KEY (`schedule_id`,`billboard_id`,`viewer_id`),
  KEY `billboard_id_idx` (`billboard_id`),
  KEY `viewer_id_idx` (`viewer_id`),
  CONSTRAINT `Scheduling_FK` FOREIGN KEY (`viewer_id`) REFERENCES `Viewers` (`viewer_id`),
  CONSTRAINT `billboard_id` FOREIGN KEY (`billboard_id`) REFERENCES `Billboards` (`billboard_id`) ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Scheduling`
--

LOCK TABLES `Scheduling` WRITE;
/*!40000 ALTER TABLE `Scheduling` DISABLE KEYS */;
/*!40000 ALTER TABLE `Scheduling` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Users` (
  `user_id` int(11) unsigned NOT NULL DEFAULT 0,
  `user_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_permissions` char(4) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `max_priority` int(11) NOT NULL,
  `password_hash` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Viewers`
--

DROP TABLE IF EXISTS `Viewers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Viewers` (
  `viewer_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `socket` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'IP + port of viewer',
  PRIMARY KEY (`viewer_id`),
  UNIQUE KEY `viewer_id_UNIQUE` (`viewer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Viewers`
--

LOCK TABLES `Viewers` WRITE;
/*!40000 ALTER TABLE `Viewers` DISABLE KEYS */;
/*!40000 ALTER TABLE `Viewers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-04-28 13:40:01
