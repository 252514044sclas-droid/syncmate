<?php
require_once __DIR__ . '/../../config/database.php';

$input = json_decode(file_get_contents('php://input'), true);
$email = $input['email'] ?? '';
$password = $input['password'] ?? '';

if (empty($email) || empty($password)) {
    echo json_encode([
        "status" => "error",
        "message" => "Email and password are required."
    ]);
    exit();
}

// Sample response structure matching Android LoginRequestDto / AuthResponseDto
echo json_encode([
    "status" => "success",
    "message" => "Login successful",
    "data" => [
        "token" => "token_subletsync_bearer_" . md5($email),
        "user" => [
            "id" => "usr_" . abs(crc32($email)),
            "name" => ucwords(str_replace('.', ' ', explode('@', $email)[0])),
            "email" => $email,
            "university" => "National Institute of Technology",
            "is_verified" => true,
            "bio" => "Student looking for campus housing.",
            "max_budget" => 15000.00
        ]
    ]
]);
?>
