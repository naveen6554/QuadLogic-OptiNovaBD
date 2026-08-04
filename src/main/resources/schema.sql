-- ===================================================================
-- OptiNova Backend - Complete Consolidated Database Schema (MySQL 8)
-- Drops old database, creates new schema, and includes all tables:
-- Core tables (users, jwt_tokens, categories, products, productimages,
-- cart_items, orders, order_items) & support tables (otp_verifications, password_reset_tokens).
-- ===================================================================

DROP DATABASE IF EXISTS `e-commerce`;
CREATE DATABASE `e-commerce` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `e-commerce`;

-- -------------------------------------------------------------------
-- 1. Table Structure: users
-- -------------------------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN','CUSTOMER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);

-- -------------------------------------------------------------------
-- 2. Table Structure: jwt_tokens
-- -------------------------------------------------------------------
CREATE TABLE jwt_tokens (
    token_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_token_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- -------------------------------------------------------------------
-- 3. Table Structure: categories
-- -------------------------------------------------------------------
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL UNIQUE
);

-- -------------------------------------------------------------------
-- 4. Table Structure: products
-- -------------------------------------------------------------------
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    category_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES categories(category_id)
);

-- -------------------------------------------------------------------
-- 5. Table Structure: productimages
-- -------------------------------------------------------------------
CREATE TABLE productimages (
    image_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    image_url TEXT NOT NULL,

    CONSTRAINT fk_image_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
        ON DELETE CASCADE
);

-- -------------------------------------------------------------------
-- 6. Table Structure: cart_items
-- -------------------------------------------------------------------
CREATE TABLE cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,

    CONSTRAINT fk_cart_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_cart_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
        ON DELETE CASCADE
);

-- -------------------------------------------------------------------
-- 7. Table Structure: orders
-- -------------------------------------------------------------------
CREATE TABLE orders (
    order_id VARCHAR(255) PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING','SUCCESS','FAILED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);

-- -------------------------------------------------------------------
-- 8. Table Structure: order_items
-- -------------------------------------------------------------------
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price_per_unit DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_orderitem_order
        FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_orderitem_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
);

-- -------------------------------------------------------------------
-- 9. Table Structure: otp_verifications
-- -------------------------------------------------------------------
CREATE TABLE `otp_verifications` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(255) NOT NULL,
    `otp_code` VARCHAR(10) NOT NULL,
    `purpose` VARCHAR(50) NOT NULL,
    `expiry_date` DATETIME NOT NULL,
    `used` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_otp_email_purpose` (`email`, `purpose`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------
-- 10. Table Structure: password_reset_tokens
-- -------------------------------------------------------------------
CREATE TABLE `password_reset_tokens` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `token` VARCHAR(255) NOT NULL UNIQUE,
    `user_id` INT NOT NULL,
    `expiry_date` DATETIME NOT NULL,
    `used` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------------
-- Initial Seed Data: Default Admin User (username: optiadmin | password: admin@123)
-- -------------------------------------------------------------------
INSERT INTO users (username, email, password, role)
VALUES ('optiadmin', 'optiadmin@optinova.com', '$2a$10$7R9rG0M7w3Kj0dZ7Yx8KueCgYvY4.0x8k7l0m1n2o3p4q5r6s7t8u', 'ADMIN')
ON DUPLICATE KEY UPDATE role='ADMIN';
