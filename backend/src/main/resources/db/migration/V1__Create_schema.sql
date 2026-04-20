CREATE TABLE `bistro` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `selector` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `lunch` (
  `id` int NOT NULL AUTO_INCREMENT,
  `bistro_id` int NOT NULL,
  `import_date` date NOT NULL,
  `dishes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `day` enum('TODAY','TOMORROW') COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('OUTDATED','NEXT_WEEK','SUCCESS','NO_DATA') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4e3pfm5g7kx28evj5ceau69jx` (`bistro_id`),
  CONSTRAINT `FK4e3pfm5g7kx28evj5ceau69jx` FOREIGN KEY (`bistro_id`) REFERENCES `bistro` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
