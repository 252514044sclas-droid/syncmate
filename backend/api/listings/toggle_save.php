<?php
require_once __DIR__ . '/../../config/database.php';

$input = json_decode(file_get_contents('php://input'), true);
$listingId = $input['listing_id'] ?? '';
$userId = "usr_dev_101";

if (empty($listingId)) {
    echo json_encode(["status" => "error", "message" => "Listing ID is required"]);
    exit();
}

try {
    // Check if already saved
    $check = $pdo->prepare("SELECT * FROM saved_listings WHERE user_id = ? AND listing_id = ?");
    $check->execute([$userId, $listingId]);

    if ($check->rowCount() > 0) {
        $del = $pdo->prepare("DELETE FROM saved_listings WHERE user_id = ? AND listing_id = ?");
        $del->execute([$userId, $listingId]);
        $saved = false;
    } else {
        $ins = $pdo->prepare("INSERT INTO saved_listings (user_id, listing_id) VALUES (?, ?)");
        $ins->execute([$userId, $listingId]);
        $saved = true;
    }

    echo json_encode([
        "status" => "success",
        "data" => $saved
    ]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Database error: " . $e->getMessage()]);
}
?>
