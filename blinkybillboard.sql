-- MariaDB dump 10.17  Distrib 10.4.10-MariaDB, for Linux (aarch64)
--
-- Host: localhost    Database: blinkyBillboard
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
-- Current Database: `blinkyBillboard`
--

USE `blinkyBillboard`;

--
-- Table structure for table `Billboards`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `Billboards` (
  `billboard_id` int(11) unsigned NOT NULL,
  `duration` int(11) unsigned NOT NULL COMMENT 'How long will one instance of this billboard be up for?',
  `creator` varchar(100) NOT NULL COMMENT 'User ID of the billboard''s creator',
  `backgroundColour` varchar(8) DEFAULT NULL,
  `messageColour` varchar(8) DEFAULT NULL,
  `informationColour` varchar(8) DEFAULT NULL,
  `message` text COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `information` text COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `billboardImage` blob DEFAULT NULL,
  PRIMARY KEY (`billboard_id`),
  KEY `fk_creator_idx` (`creator`),
  CONSTRAINT `Billboards_FK` FOREIGN KEY (`creator`) REFERENCES `Users` (`user_name`)
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

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `Scheduling` (
  `schedule_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The ID is per billboard, per viewer',
  `billboard_id` int(11) unsigned NOT NULL,
  `viewer_id` int(11) unsigned NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `interval` int(11) unsigned NOT NULL COMMENT 'How often should the billboard repeat itself in minutes? (must be more than the duration )',
  `scheduled_at` time NOT NULL COMMENT 'When was the billboard created?',
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

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `Users` (
  `user_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_permissions` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password_hash` binary(32) NOT NULL,
  `salt` binary(100) NOT NULL,
  PRIMARY KEY (`user_name`)
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

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `Viewers` (
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

-- Dump completed on 2020-05-05 22:54:48
