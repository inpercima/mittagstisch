-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: mysql
-- Erstellungszeit: 20. Okt 2022 um 12:51
-- Server-Version: 8.0.30
-- PHP-Version: 8.0.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `mittagstisch`
--

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `location`
--
ALTER TABLE `location` MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
COMMIT;

--
-- Daten für Tabelle `location`
--

INSERT INTO `location` VALUES(1, 'Bistro im BIC');
INSERT INTO `location` VALUES(2, 'Cafeteria M9');
INSERT INTO `location` VALUES(3, 'Bistro am Kanal');
INSERT INTO `location` VALUES(4, 'Kirow Kantine');
INSERT INTO `location` VALUES(5, 'Geschmackssache');
INSERT INTO `location` VALUES(6, 'Lebensmittel & Imbiss Seidel');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
