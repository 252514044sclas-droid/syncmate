<?php
require_once __DIR__ . '/../../config/database.php';

echo json_encode([
    "status" => "success",
    "data" => [
        [
            "id" => "lst_1",
            "title" => "Modern 1BHK Sublet Near Engineering Campus",
            "address" => "Adyar, Chennai",
            "price_monthly" => 14500.0,
            "bedrooms" => 1,
            "bathrooms" => 1.0,
            "room_type" => "Private Room",
            "is_furnished" => true,
            "utilities_included" => true,
            "duration" => "May 15 - Aug 15, 2025",
            "description" => "Spacious private bedroom with attached bath in a modern apartment near IIT Madras / Anna University. Fully furnished with high-speed WiFi included.",
            "owner_name" => "Sarah Chen",
            "owner_email" => "sarah.c@university.edu",
            "owner_phone" => "+91 98401 23456",
            "is_verified_student" => true,
            "distance_to_campus" => "0.4 km",
            "risk_score" => 4,
            "predicted_fair_rent" => 14000.0,
            "is_saved" => true
        ],
        [
            "id" => "lst_2",
            "title" => "Luxury Studio at Student Hub",
            "address" => "Koramangala, Bengaluru",
            "price_monthly" => 22000.0,
            "bedrooms" => 1,
            "bathrooms" => 1.0,
            "room_type" => "Entire Studio",
            "is_furnished" => true,
            "utilities_included" => false,
            "duration" => "June 1 - Aug 31, 2025",
            "description" => "Full studio apartment available for summer sublease near tech park and colleges.",
            "owner_name" => "Marcus Vance",
            "owner_email" => "mvance@student.edu",
            "owner_phone" => "+91 98801 34567",
            "is_verified_student" => true,
            "distance_to_campus" => "0.8 km",
            "risk_score" => 8,
            "predicted_fair_rent" => 21500.0,
            "is_saved" => false
        ]
    ]
]);
?>
