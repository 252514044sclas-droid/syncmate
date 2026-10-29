-- SubletSync MySQL Database Schema
CREATE DATABASE IF NOT EXISTS `subletsync_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `subletsync_db`;

-- Users Table
CREATE TABLE IF NOT EXISTS `users` (
    `id` VARCHAR(64) PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(120) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `university` VARCHAR(150) NOT NULL,
    `is_verified` TINYINT(1) DEFAULT 0,
    `bio` TEXT,
    `preferred_lease_term` VARCHAR(50) DEFAULT 'Summer 2025',
    `max_budget` DECIMAL(10,2) DEFAULT 15000.00,
    `preferred_location` VARCHAR(150) DEFAULT 'Adyar / Campus',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Housing Listings Table
CREATE TABLE IF NOT EXISTS `listings` (
    `id` VARCHAR(64) PRIMARY KEY,
    `owner_id` VARCHAR(64) NOT NULL,
    `title` VARCHAR(150) NOT NULL,
    `address` VARCHAR(200) NOT NULL,
    `price_monthly` DECIMAL(10,2) NOT NULL,
    `bedrooms` INT DEFAULT 1,
    `bathrooms` DECIMAL(3,1) DEFAULT 1.0,
    `room_type` VARCHAR(50) DEFAULT 'Private Room',
    `is_furnished` TINYINT(1) DEFAULT 1,
    `utilities_included` TINYINT(1) DEFAULT 0,
    `duration` VARCHAR(80) DEFAULT 'May 15 - Aug 15, 2025',
    `description` TEXT NOT NULL,
    `owner_name` VARCHAR(100) NOT NULL,
    `owner_email` VARCHAR(120) NOT NULL,
    `owner_phone` VARCHAR(20) DEFAULT '',
    `distance_to_campus` VARCHAR(50) DEFAULT '0.5 km',
    `risk_score` INT DEFAULT 5,
    `predicted_fair_rent` DECIMAL(10,2) DEFAULT 14000.00,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`owner_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Saved Listings Junction
CREATE TABLE IF NOT EXISTS `saved_listings` (
    `user_id` VARCHAR(64) NOT NULL,
    `listing_id` VARCHAR(64) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`, `listing_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`listing_id`) REFERENCES `listings`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Seed Sample Data
INSERT INTO `users` (`id`, `name`, `email`, `password_hash`, `university`, `is_verified`, `bio`) VALUES
('usr_dev_101', 'Alex Morgan', 'alex.morgan@university.edu', '$2y$10$e8T.0p9hZgYm5uW2t9...', 'National Institute of Technology', 1, 'Computer Science student looking for summer sublet near campus.');

INSERT INTO `listings` (`id`, `owner_id`, `title`, `address`, `price_monthly`, `bedrooms`, `bathrooms`, `room_type`, `owner_name`, `owner_email`, `distance_to_campus`) VALUES
('lst_1', 'usr_dev_101', 'Modern 1BHK Sublet Near Engineering Campus', 'Adyar, Chennai', 14500.00, 1, 1.0, 'Private Room', 'Sarah Chen', 'sarah.c@university.edu', '0.4 km'),
('lst_2', 'usr_dev_101', 'Luxury Studio at Student Hub', 'Koramangala, Bengaluru', 22000.00, 1, 1.0, 'Entire Studio', 'Marcus Vance', 'mvance@student.edu', '0.8 km'),
('lst_3', 'usr_dev_101', 'Cozy Private Room in 3BHK Flat', 'Kothrud, Pune', 9500.00, 3, 2.0, 'Private Room', 'Emily Rodriguez', 'emily.r@university.edu', '1.2 km');
