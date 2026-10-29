-- ========================================================
-- SubletSync Production-Grade MySQL Database Schema (15 Tables)
-- ========================================================

DROP DATABASE IF EXISTS `subletsync_db`;
CREATE DATABASE `subletsync_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `subletsync_db`;

-- 1. Users Table (Student Profiles & Verification)
CREATE TABLE `users` (
    `id` VARCHAR(64) PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(120) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `university` VARCHAR(150) NOT NULL,
    `is_verified` TINYINT(1) DEFAULT 1,
    `profile_picture_url` VARCHAR(255) DEFAULT NULL,
    `bio` TEXT,
    `preferred_lease_term` VARCHAR(50) DEFAULT 'Summer 2025',
    `max_budget` DECIMAL(10,2) DEFAULT 15000.00,
    `preferred_location` VARCHAR(150) DEFAULT 'Adyar / Campus',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Housing Listings Table (Sublet Posts)
CREATE TABLE `listings` (
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
    `is_verified_student` TINYINT(1) DEFAULT 1,
    `distance_to_campus` VARCHAR(50) DEFAULT '0.5 km',
    `risk_score` INT DEFAULT 5,
    `predicted_fair_rent` DECIMAL(10,2) DEFAULT 14000.00,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`owner_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Listing Photo Gallery Images Table
CREATE TABLE `listing_images` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `listing_id` VARCHAR(64) NOT NULL,
    `image_url` VARCHAR(255) NOT NULL,
    `display_order` INT DEFAULT 1,
    `caption` VARCHAR(150) DEFAULT NULL,
    FOREIGN KEY (`listing_id`) REFERENCES `listings`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Listing Amenities Table
CREATE TABLE `listing_amenities` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `listing_id` VARCHAR(64) NOT NULL,
    `amenity_name` VARCHAR(80) NOT NULL,
    FOREIGN KEY (`listing_id`) REFERENCES `listings`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Saved Listings Table (Bookmarked Sublets)
CREATE TABLE `saved_listings` (
    `user_id` VARCHAR(64) NOT NULL,
    `listing_id` VARCHAR(64) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`, `listing_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`listing_id`) REFERENCES `listings`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. Roommate Profiles Table (Compatibility Matching)
CREATE TABLE `roommates` (
    `id` VARCHAR(64) PRIMARY KEY,
    `user_id` VARCHAR(64) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `university` VARCHAR(150) NOT NULL,
    `avatar_url` VARCHAR(255) DEFAULT NULL,
    `compatibility_percentage` INT DEFAULT 90,
    `match_reason` VARCHAR(255) NOT NULL,
    `lifestyle_rhythm` VARCHAR(150) NOT NULL,
    `max_budget_monthly` DECIMAL(10,2) NOT NULL,
    `preferred_location` VARCHAR(150) NOT NULL,
    `bio` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. Tour Schedules Table (Virtual & In-Person Property Tours)
CREATE TABLE `tour_schedules` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `listing_id` VARCHAR(64) NOT NULL,
    `student_id` VARCHAR(64) NOT NULL,
    `tour_type` VARCHAR(30) DEFAULT 'IN_PERSON', -- IN_PERSON or VIRTUAL
    `tour_date` DATETIME NOT NULL,
    `status` VARCHAR(20) DEFAULT 'PENDING', -- PENDING, CONFIRMED, CANCELLED
    `notes` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`listing_id`) REFERENCES `listings`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`student_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. Rent Predictions Table (AI Fair-Price ML Logs)
CREATE TABLE `rent_predictions` (
    `id` VARCHAR(64) PRIMARY KEY,
    `user_id` VARCHAR(64) NOT NULL,
    `location` VARCHAR(150) NOT NULL,
    `square_feet` INT NOT NULL,
    `bedrooms` INT NOT NULL,
    `room_type` VARCHAR(50) NOT NULL,
    `predicted_fair_rent` DECIMAL(10,2) NOT NULL,
    `rent_range_low` DECIMAL(10,2) NOT NULL,
    `rent_range_high` DECIMAL(10,2) NOT NULL,
    `estimated_utilities_monthly` DECIMAL(10,2) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 9. Contract Analyses Table (Contract Guard Legal Scan Logs)
CREATE TABLE `contract_analyses` (
    `id` VARCHAR(64) PRIMARY KEY,
    `user_id` VARCHAR(64) NOT NULL,
    `file_name` VARCHAR(200) NOT NULL,
    `lease_term` VARCHAR(100) NOT NULL,
    `monthly_rent` DECIMAL(10,2) NOT NULL,
    `security_deposit` DECIMAL(10,2) NOT NULL,
    `notice_period_days` INT DEFAULT 30,
    `overall_risk_score` INT DEFAULT 20,
    `summary_overview` TEXT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 10. Flagged Contract Clauses Table
CREATE TABLE `flagged_clauses` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `contract_id` VARCHAR(64) NOT NULL,
    `title` VARCHAR(150) NOT NULL,
    `clause_text` TEXT NOT NULL,
    `severity` VARCHAR(20) DEFAULT 'MEDIUM',
    `explanation` TEXT NOT NULL,
    `recommendation` TEXT NOT NULL,
    FOREIGN KEY (`contract_id`) REFERENCES `contract_analyses`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 11. Fake Listing Scans Table (Scam Shield Analysis Logs)
CREATE TABLE `fake_listing_scans` (
    `id` VARCHAR(64) PRIMARY KEY,
    `user_id` VARCHAR(64) NOT NULL,
    `listing_id` VARCHAR(64) DEFAULT NULL,
    `risk_score` INT NOT NULL,
    `risk_level` VARCHAR(30) NOT NULL,
    `explanation` TEXT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 12. Interactive Lease Checklists Table
CREATE TABLE `lease_checklists` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` VARCHAR(64) NOT NULL,
    `item_text` VARCHAR(255) NOT NULL,
    `is_completed` TINYINT(1) DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 13. Student Peer Reviews & Ratings Table
CREATE TABLE `user_reviews` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `reviewer_id` VARCHAR(64) NOT NULL,
    `target_user_id` VARCHAR(64) NOT NULL,
    `rating_stars` INT DEFAULT 5,
    `review_text` TEXT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`reviewer_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`target_user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 14. Chat Messages Table (Student Peer Messaging)
CREATE TABLE `chat_messages` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `sender_id` VARCHAR(64) NOT NULL,
    `receiver_id` VARCHAR(64) NOT NULL,
    `message_text` TEXT NOT NULL,
    `sent_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`sender_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`receiver_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 15. Notifications Table
CREATE TABLE `notifications` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` VARCHAR(64) NOT NULL,
    `title` VARCHAR(150) NOT NULL,
    `message` TEXT NOT NULL,
    `is_read` TINYINT(1) DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ========================================================
-- Seed Initial Sample Data
-- ========================================================

INSERT INTO `users` (`id`, `name`, `email`, `password_hash`, `university`, `is_verified`, `bio`) VALUES
('usr_dev_101', 'Alex Morgan', 'alex.morgan@university.edu', '$2y$10$e8T.0p9hZgYm5uW2t9...', 'National Institute of Technology', 1, 'Computer Science student looking for summer sublet near campus.'),
('usr_dev_102', 'Maya Lin', 'maya.l@university.edu', '$2y$10$e8T.0p9hZgYm5uW2t9...', 'Anna University', 1, 'CS junior looking for a clean, peaceful sublet room for summer internship.'),
('usr_dev_103', 'Jordan Patel', 'jordan.p@student.edu', '$2y$10$e8T.0p9hZgYm5uW2t9...', 'IIT Madras', 1, 'Grad student in ECE. Friendly, respectful, and loves cooking on weekends.');

INSERT INTO `listings` (`id`, `owner_id`, `title`, `address`, `price_monthly`, `bedrooms`, `bathrooms`, `room_type`, `owner_name`, `owner_email`, `distance_to_campus`) VALUES
('lst_1', 'usr_dev_101', 'Modern 1BHK Sublet Near Engineering Campus', 'Adyar, Chennai', 14500.00, 1, 1.0, 'Private Room', 'Sarah Chen', 'sarah.c@university.edu', '0.4 km'),
('lst_2', 'usr_dev_102', 'Luxury Studio at Student Hub', 'Koramangala, Bengaluru', 22000.00, 1, 1.0, 'Entire Studio', 'Marcus Vance', 'mvance@student.edu', '0.8 km'),
('lst_3', 'usr_dev_103', 'Cozy Private Room in 3BHK Flat', 'Kothrud, Pune', 9500.00, 3, 2.0, 'Private Room', 'Emily Rodriguez', 'emily.r@university.edu', '1.2 km');

INSERT INTO `listing_amenities` (`listing_id`, `amenity_name`) VALUES
('lst_1', 'Air Conditioning'), ('lst_1', 'High-Speed Broadband WiFi'), ('lst_1', 'Power Backup'), ('lst_1', 'Washing Machine'),
('lst_2', 'Rooftop Terrace'), ('lst_2', 'Gym Access'), ('lst_2', 'Security Guard'), ('lst_2', 'Elevator');

INSERT INTO `saved_listings` (`user_id`, `listing_id`) VALUES
('usr_dev_101', 'lst_1');

INSERT INTO `roommates` (`id`, `user_id`, `name`, `university`, `compatibility_percentage`, `match_reason`, `lifestyle_rhythm`, `max_budget_monthly`, `preferred_location`, `bio`) VALUES
('rm_1', 'usr_dev_102', 'Maya Lin', 'Anna University \'26', 96, 'Matches your quiet study hours & budget', 'Early Riser • Non-Smoker', 12000.00, 'Adyar / Guindy', 'CS junior looking for a clean, peaceful sublet room for summer internship.'),
('rm_2', 'usr_dev_103', 'Jordan Patel', 'IIT Madras \'25', 91, 'Similar lease term & campus quadrant', 'Night Owl • Quiet & Organized', 16500.00, 'Koramangala, Bengaluru', 'Grad student in ECE. Friendly, respectful, and loves cooking on weekends.');

INSERT INTO `tour_schedules` (`listing_id`, `student_id`, `tour_type`, `tour_date`, `status`, `notes`) VALUES
('lst_1', 'usr_dev_101', 'IN_PERSON', '2025-05-10 14:00:00', 'CONFIRMED', 'Meeting at property entrance near main gate.');

INSERT INTO `lease_checklists` (`user_id`, `item_text`, `is_completed`) VALUES
('usr_dev_101', 'Verify student ID and university enrollment of sublet host.', 1),
('usr_dev_101', 'Run Contract Guard scan on sublease agreement PDF.', 1),
('usr_dev_101', 'Confirm utility responsibilities (electricity, water, wifi).', 0),
('usr_dev_101', 'Inspect room condition and document pre-existing damage with photos.', 0);

INSERT INTO `user_reviews` (`reviewer_id`, `target_user_id`, `rating_stars`, `review_text`) VALUES
('usr_dev_101', 'usr_dev_102', 5, 'Maya is extremely clean, organized, and respectful. Great roommate!');

INSERT INTO `notifications` (`user_id`, `title`, `message`) VALUES
('usr_dev_101', 'Inquiry Reply', 'Sarah replied to your inquiry regarding College Ave Sublet.'),
('usr_dev_101', 'Price Drop Alert', 'AI Fair-Rent Alert: Sublet price in your saved search dropped by 5%.'),
('usr_dev_101', 'Contract Scan Complete', 'Contract Guard scan complete for Sublease_Agreement.pdf.');
