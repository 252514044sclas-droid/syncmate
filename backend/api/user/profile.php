<?php
require_once __DIR__ . '/../../config/database.php';

try {
    $stmt = $pdo->query("SELECT * FROM users WHERE id = 'usr_dev_101' LIMIT 1");
    $user = $stmt->fetch();

    if ($user) {
        echo json_encode([
            "status" => "success",
            "data" => [
                "id" => $user['id'],
                "name" => $user['name'],
                "email" => $user['email'],
                "university" => $user['university'],
                "is_verified" => (bool)$user['is_verified'],
                "bio" => $user['bio'],
                "preferred_lease_term" => $user['preferred_lease_term'],
                "max_budget" => (float)$user['max_budget'],
                "preferred_location" => $user['preferred_location']
            ]
        ]);
    } else {
        echo json_encode(["status" => "error", "message" => "User not found"]);
    }
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Database error: " . $e->getMessage()]);
}
?>
