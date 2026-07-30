-- ===================================================================
-- OptiNova Backend - Additional Tables Script (MySQL 8)
-- Use this script in MySQL Workbench to add OTP & Token tables.
-- Existing tables (users, categories, products, etc.) are NOT modified or recreated.
-- ===================================================================

USE `e-commerce`;

-- -------------------------------------------------------------------
-- Table Structure: otp_verifications
-- Used for OTP verification during User Registration and Password Reset
-- -------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `otp_verifications` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(100) NOT NULL,
    `otp_code` VARCHAR(10) NOT NULL,
    `purpose` VARCHAR(50) NOT NULL COMMENT 'REGISTRATION, PASSWORD_RESET',
    `expiry_date` DATETIME NOT NULL,
    `used` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_otp_email_purpose` (`email`, `purpose`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------
-- Ensure users table has is_verified column for OTP verification status
-- -------------------------------------------------------------------
SET @dbname = DATABASE();
SET @tablename = 'users';
SET @columnname = 'is_verified';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      TABLE_SCHEMA = @dbname
      AND TABLE_NAME = @tablename
      AND COLUMN_NAME = @columnname
  ) > 0,
  'SELECT 1',
  'ALTER TABLE users ADD COLUMN is_verified BOOLEAN NOT NULL DEFAULT FALSE'
));
PREPARE addColumnIfNotExists FROM @preparedStatement;
EXECUTE addColumnIfNotExists;
DEALLOCATE PREPARE addColumnIfNotExists;
