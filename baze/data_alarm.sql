-- MySQL dump 10.13  Distrib 8.0.13, for Win64 (x86_64)
--
-- Host: localhost    Database: data
-- ------------------------------------------------------
-- Server version	8.0.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alarm`
--

DROP TABLE IF EXISTS `alarm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `alarm` (
  `id` int(11) NOT NULL,
  `nazivAlarma` varchar(255) NOT NULL,
  `nazivZvona` varchar(255) NOT NULL,
  `onoff` int(11) DEFAULT NULL,
  `periodZvonjave` int(11) NOT NULL,
  `vremeAlarma` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alarm`
--

LOCK TABLES `alarm` WRITE;
/*!40000 ALTER TABLE `alarm` DISABLE KEYS */;
INSERT INTO `alarm` VALUES (1,'Skola','Set Fire to the rain',0,0,'2019-02-13 01:42:10'),(2,'NoviAlarm','Behind Blue Eyes',1,0,'2019-02-13 01:56:00'),(3,'Alarmius','Ring ring',1,0,'2019-02-13 01:58:00'),(4,'Kukulele','Sweet Dreams',0,0,'2019-02-13 02:03:00'),(5,'Novina','Smack that',1,0,'2019-02-14 02:11:00'),(6,'smakara','I did it my way',1,0,'2019-02-14 02:32:37'),(7,'Danak','Ringispil',1,0,'0119-01-13 03:08:22'),(8,'Pljacka','Ringispil',1,0,'0119-01-13 01:58:54'),(9,'Nista','Ringispil',1,0,'0119-01-15 11:51:55'),(10,'Robbery','Ringispil',1,0,'0119-01-13 04:48:55'),(11,'Tumaranje','Ringispil',1,0,'0119-01-14 13:04:12'),(12,'Lencarenje','Ringispil',1,0,'2019-02-14 02:29:12'),(13,'UkljuciAlarm','Ringispil',0,0,'2019-02-13 03:34:17'),(14,'Rucak','Ringispil',1,0,'2019-02-13 17:22:43'),(15,'Sastanak','Ringispil',1,0,'2019-05-15 10:24:12'),(16,'Svadba','Ringispil',1,0,'2019-05-15 09:24:13'),(17,'Rodjenje','Ringispil',1,0,'2019-10-07 13:38:55'),(18,'Svadba','Ringispil',1,0,'2019-07-10 13:04:13'),(19,'Svadba','Ringispil',1,0,'2019-05-15 10:08:55'),(20,'Svadba','Ringispil',1,0,'2019-05-15 10:08:55'),(21,'NovaObaveza','Ringispil',0,0,'2019-02-13 12:25:13');
/*!40000 ALTER TABLE `alarm` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-02-13 15:14:48
