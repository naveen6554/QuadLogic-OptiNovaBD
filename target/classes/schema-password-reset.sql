-- ===================================================================
-- OptiNova Backend - Password Reset Token Script (MySQL 8)
-- Stores UUID Tokens for Password Reset Workflow with 15-Minute Expiry
-- ===================================================================

USE `e-commerce`;

CREATE TABLE IF NOT EXISTS `password_reset_tokens` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `token` VARCHAR(100) NOT NULL UNIQUE,
    `user_id` BIGINT NOT NULL,
    `expiry_date` DATETIME NOT NULL,
    `used` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_password_reset_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
