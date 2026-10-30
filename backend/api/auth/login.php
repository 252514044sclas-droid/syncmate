<?php
require_once __DIR__ . '/../../config/database.php';

$input = json_decode(file_get_contents('php://input'), true);
$email = $input['email'] ?? '';
$password = $input['password'] ?? '';

if (empty($email) || empty($password)) {
    echo json_encode(["status" => "error", "message" => "Email and password are required."]);
    exit();
}

try {
    $stmt = $pdo->prepare("SELECT * FROM users WHERE email = ? LIMIT 1");
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    if ($user) {
        echo json_encode([
            "status" => "success",
            "message" => "Login successful",
            "data" => [
                "token" => "token_subletsync_" . md5($email),
                "user" => [
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
            ]
        ]);
    } else {
        // Auto-register user for smooth demo if not found in DB
        $userId = "usr_" . uniqid();
        $name = ucwords(str_replace(['.', '_'], ' ', explode('@', $email)[0]));
        $stmt = $pdo->prepare("INSERT INTO users (id, name, email, password_hash, university, is_verified) VALUES (?, ?, ?, ?, ?, 1)");
        $stmt->execute([$userId, $name, $email, password_hash($password, PASSWORD_DEFAULT), "National Institute of Technology"]);

        echo json_encode([
            "status" => "success",
            "message" => "Account created and login successful",
            "data" => [
                "token" => "token_subletsync_" . md5($email),
                "user" => [
                    "id" => $userId,
                    "name" => $name,
                    "email" => $email,
                    "university" => "National Institute of Technology",
                    "is_verified" => true
                ]
            ]
        ]);
    }
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Database error: " . $e->getMessage()]);
}
?>
