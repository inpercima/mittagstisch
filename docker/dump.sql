-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: mysql
-- Erstellungszeit: 19. Feb 2026 um 20:41
-- Server-Version: 8.0.44
-- PHP-Version: 8.3.30

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

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `bistro`
--

CREATE TABLE `bistro` (
  `id` int NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `selector` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Daten für Tabelle `bistro`
--

INSERT INTO `bistro` (`id`, `name`, `selector`, `url`) VALUES
(1, 'Bistro im BIC', 'main#content_main', 'http://www.bistro-bic.de/wochenkarte'),
(2, 'Rocky Maria - Kantine Tapetenwerk', 'main div section.info-box', 'https://rocky-maria.de/kantine'),
(3, 'Geschmackssache Leipzig', '#main > .entry:first-of-type table', 'https://geschmackssache-leipzig.de'),
(4, 'BIOMARE', 'main section#mittagstisch', 'https://www.bio-mare.com/'),
(5, 'Kirow Kantine', 'div#c21', 'https://www.kirow-kantine.de/');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `lunch`
--

CREATE TABLE `lunch` (
  `bistro_id` int NOT NULL,
  `id` int NOT NULL,
  `import_date` date NOT NULL,
  `lunches` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `day` enum('TODAY','TOMORROW') COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('OUTDATED','NEXT_WEEK','SUCCESS','NO_DATA') COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `bistro`
--
ALTER TABLE `bistro`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `lunch`
--
ALTER TABLE `lunch`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK4e3pfm5g7kx28evj5ceau69jx` (`bistro_id`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `bistro`
--
ALTER TABLE `bistro`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT für Tabelle `lunch`
--
ALTER TABLE `lunch`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `lunch`
--
ALTER TABLE `lunch`
  ADD CONSTRAINT `FK4e3pfm5g7kx28evj5ceau69jx` FOREIGN KEY (`bistro_id`) REFERENCES `bistro` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
