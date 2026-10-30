<?php
require_once __DIR__ . '/../../config/database.php';

$input = json_decode(file_get_contents('php://input'), true);
$name = $input['name'] ?? 'Student';
$email = $input['email'] ?? 'student@university.edu';
$password = $input['password'] ?? 'password';
$university = $input['university'] ?? 'National Institute of Technology';

if (empty($email)) {
    echo json_encode(["status" => "error", "message" => "Email is required."]);
    exit();
}

$userId = "usr_" . abs(crc32($email));
$passwordHash = password_hash($password, PASSWORD_DEFAULT);
$isVerified = 1;

try {
    $stmt = $pdo->prepare("INSERT INTO users (id, name, email, password_hash, university, is_verified) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = ?, university = ?");
    $stmt->execute([$userId, $name, $email, $passwordHash, $university, $isVerified, $name, $university]);

    echo json_encode([
        "status" => "success",
        "message" => "User registered and stored in MySQL successfully",
        "data" => [
            "token" => "token_subletsync_" . md5($email),
            "user" => [
                "id" => $userId,
                "name" => $name,
                "email" => $email,
                "university" => $university,
                "is_verified" => (bool)$isVerified
            ]
        ]
    ]);
} catch (PDOException $e) {
    echo json_encode([
        "status" => "error",
        "message" => "Database error: " . $e->getMessage()
    ]);
}
?>
