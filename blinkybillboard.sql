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
LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;

DROP TABLE IF EXISTS `Billboards`;
CREATE TABLE `Billboards` (
  `billboard_id` int(11) unsigned NOT NULL,
  `duration` int(11) unsigned NOT NULL COMMENT 'How long will one instance of this billboard be up for?',
  `creator` int(11) unsigned NOT NULL COMMENT 'User ID of the billboard''s creator',
  `XML` text COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'The billboard''s XML data - stored directly rather than as an external file',
  PRIMARY KEY (`billboard_id`),
  KEY `fk_creator_idx` (`creator`),
  CONSTRAINT `fk_creator` FOREIGN KEY (`creator`) REFERENCES `Users` (`user_id`) ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `Billboards`
--

LOCK TABLES `Billboards` WRITE;
/*!40000 ALTER TABLE `Billboards` DISABLE KEYS */;
/*!40000 ALTER TABLE `Billboards` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Table structure for table `Viewers`
--

DROP TABLE IF EXISTS `Viewers`;
CREATE TABLE `Viewers` (
  `viewer_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `socket` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'IP + port of viewer',
  PRIMARY KEY (`viewer_id`),
  UNIQUE KEY `viewer_id_UNIQUE` (`viewer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `Viewers`
--

LOCK TABLES `Viewers` WRITE;
/*!40000 ALTER TABLE `Viewers` DISABLE KEYS */;
/*!40000 ALTER TABLE `Viewers` ENABLE KEYS */;
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
  `scheduled_at` time NOT NULL DEFAULT '00:00:00' COMMENT 'When was the billboard created?',
  PRIMARY KEY (`schedule_id`,`billboard_id`,`viewer_id`),
  KEY `billboard_id_idx` (`billboard_id`),
  KEY `viewer_id_idx` (`viewer_id`),
  CONSTRAINT `Scheduling_FK` FOREIGN KEY (`viewer_id`) REFERENCES `Viewers` (`viewer_id`),
  CONSTRAINT `billboard_id` FOREIGN KEY (`billboard_id`) REFERENCES `Billboards` (`billboard_id`) ON DELETE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `Scheduling`
--

LOCK TABLES `Scheduling` WRITE;
/*!40000 ALTER TABLE `Scheduling` DISABLE KEYS */;
/*!40000 ALTER TABLE `Scheduling` ENABLE KEYS */;
UNLOCK TABLES;
