<?php
require_once __DIR__ . '/../../config/database.php';

$input = json_decode(file_get_contents('php://input'), true);
$name = $input['name'] ?? '';
$email = $input['email'] ?? '';
$password = $input['password'] ?? '';
$university = $input['university'] ?? 'National Institute of Technology';

if (empty($name) || empty($email) || empty($password)) {
    echo json_encode(["status" => "error", "message" => "Name, email, and password are required."]);
    exit();
}

$userId = "usr_" . uniqid();
$passwordHash = password_hash($password, PASSWORD_DEFAULT);
$isVerified = (str_contains($email, '.edu') || str_contains($email, 'university')) ? 1 : 0;

try {
    $stmt = $pdo->prepare("INSERT INTO users (id, name, email, password_hash, university, is_verified) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->execute([$userId, $name, $email, $passwordHash, $university, $isVerified]);

    echo json_encode([
        "status" => "success",
        "message" => "User registered successfully",
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
        "message" => "Email already exists or DB error: " . $e->getMessage()
    ]);
}
?>
