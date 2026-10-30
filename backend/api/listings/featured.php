<?php
require_once __DIR__ . '/../../config/database.php';

try {
    $stmt = $pdo->query("SELECT * FROM listings ORDER BY created_at DESC");
    $listings = $stmt->fetchAll();

    $formattedListings = [];
    foreach ($listings as $row) {
        $formattedListings[] = [
            "id" => $row['id'],
            "title" => $row['title'],
            "address" => $row['address'],
            "price_monthly" => (float)$row['price_monthly'],
            "bedrooms" => (int)$row['bedrooms'],
            "bathrooms" => (float)$row['bathrooms'],
            "room_type" => $row['room_type'],
            "is_furnished" => (bool)$row['is_furnished'],
            "utilities_included" => (bool)$row['utilities_included'],
            "duration" => $row['duration'],
            "description" => $row['description'],
            "owner_name" => $row['owner_name'],
            "owner_email" => $row['owner_email'],
            "owner_phone" => $row['owner_phone'],
            "is_verified_student" => (bool)$row['is_verified_student'],
            "distance_to_campus" => $row['distance_to_campus'],
            "risk_score" => (int)$row['risk_score'],
            "predicted_fair_rent" => (float)$row['predicted_fair_rent'],
            "is_saved" => false
        ];
    }

    echo json_encode([
        "status" => "success",
        "data" => $formattedListings
    ]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Database error: " . $e->getMessage()]);
}
?>
