<?php
require_once __DIR__ . '/../../config/database.php';

$input = json_decode(file_get_contents('php://input'), true);
$title = $input['title'] ?? 'Student Sublet';
$address = $input['address'] ?? 'Campus Area';
$priceMonthly = $input['price_monthly'] ?? 14000.0;
$bedrooms = $input['bedrooms'] ?? 1;
$bathrooms = $input['bathrooms'] ?? 1.0;
$roomType = $input['room_type'] ?? 'Private Room';
$description = $input['description'] ?? 'Verified student housing unit.';

$id = "lst_" . uniqid();
$ownerId = "usr_dev_101";

try {
    $stmt = $pdo->prepare("INSERT INTO listings (id, owner_id, title, address, price_monthly, bedrooms, bathrooms, room_type, description, owner_name, owner_email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'Alex Morgan', 'alex.morgan@university.edu')");
    $stmt->execute([$id, $ownerId, $title, $address, $priceMonthly, $bedrooms, $bathrooms, $roomType, $description]);

    echo json_encode([
        "status" => "success",
        "message" => "Listing created and saved to MySQL successfully",
        "data" => [
            "id" => $id,
            "title" => $title,
            "address" => $address,
            "price_monthly" => (float)$priceMonthly,
            "bedrooms" => (int)$bedrooms,
            "bathrooms" => (float)$bathrooms,
            "room_type" => $roomType,
            "description" => $description,
            "owner_name" => "Alex Morgan",
            "owner_email" => "alex.morgan@university.edu",
            "is_verified_student" => true,
            "distance_to_campus" => "0.4 km"
        ]
    ]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Database error: " . $e->getMessage()]);
}
?>
