ALTER TABLE `bistro`
ADD COLUMN type ENUM('TEXT', 'IMAGE', 'PDF') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TEXT';

UPDATE `bistro`
SET type = CASE
  WHEN image_selector IS NOT NULL THEN 'IMAGE'
  WHEN document_selector IS NOT NULL THEN 'PDF'
  ELSE 'TEXT'
END;

UPDATE `bistro`
SET selector = CASE
  WHEN image_selector IS NOT NULL THEN image_selector
  WHEN document_selector IS NOT NULL THEN document_selector
  ELSE selector
END;
