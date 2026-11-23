<?php
require_once __DIR__ . '/../../config/database.php';
header('Content-Type: application/json; charset=UTF-8');

$input = json_decode(file_get_contents('php://input'), true) ?? [];
$requestId = $input['request_id'] ?? ('req_' . time() . '_' . rand(100, 999));
$senderId = $input['sender_id'] ?? '';
$senderName = $input['sender_name'] ?? '';
$senderUniversity = $input['sender_university'] ?? '';
$receiverId = $input['receiver_id'] ?? '';
$receiverName = $input['receiver_name'] ?? '';
$compatibilityScore = intval($input['compatibility_score'] ?? 85);
$introMessage = $input['intro_message'] ?? '';
$status = 'PENDING';
$createdAt = round(microtime(true) * 1000);

if (!$senderId || !$receiverId) {
    echo json_encode(["status" => "error", "message" => "sender_id and receiver_id are required"]);
    exit();
}

try {
    if ($pdo) {
        $stmt = $pdo->prepare("INSERT INTO roommate_requests (request_id, sender_id, sender_name, sender_university, receiver_id, receiver_name, compatibility_score, intro_message, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->execute([$requestId, $senderId, $senderName, $senderUniversity, $receiverId, $receiverName, $compatibilityScore, $introMessage, $status, $createdAt]);
    }

    echo json_encode([
        "status" => "success",
        "data" => [
            "request_id" => $requestId,
            "sender_id" => $senderId,
            "sender_name" => $senderName,
            "sender_university" => $senderUniversity,
            "receiver_id" => $receiverId,
            "receiver_name" => $receiverName,
            "compatibility_score" => $compatibilityScore,
            "intro_message" => $introMessage,
            "status" => $status,
            "timestamp" => $createdAt
        ]
    ]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
