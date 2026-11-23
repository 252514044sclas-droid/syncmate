<?php
require_once __DIR__ . '/../../config/database.php';
header('Content-Type: application/json; charset=UTF-8');

$userId = $_GET['user_id'] ?? '';

if (!$userId) {
    echo json_encode(["status" => "error", "message" => "user_id is required"]);
    exit();
}

try {
    $incoming = [];
    $sent = [];

    if ($pdo) {
        $stmtIn = $pdo->prepare("SELECT * FROM roommate_requests WHERE receiver_id = ? ORDER BY created_at DESC");
        $stmtIn->execute([$userId]);
        foreach ($stmtIn->fetchAll(PDO::FETCH_ASSOC) as $row) {
            $incoming[] = [
                "request_id" => $row['request_id'],
                "sender_id" => $row['sender_id'],
                "sender_name" => $row['sender_name'],
                "sender_university" => $row['sender_university'],
                "receiver_id" => $row['receiver_id'],
                "receiver_name" => $row['receiver_name'],
                "compatibility_score" => (int)$row['compatibility_score'],
                "intro_message" => $row['intro_message'] ?? '',
                "status" => $row['status'],
                "timestamp" => (int)$row['created_at']
            ];
        }

        $stmtSent = $pdo->prepare("SELECT * FROM roommate_requests WHERE sender_id = ? ORDER BY created_at DESC");
        $stmtSent->execute([$userId]);
        foreach ($stmtSent->fetchAll(PDO::FETCH_ASSOC) as $row) {
            $sent[] = [
                "request_id" => $row['request_id'],
                "sender_id" => $row['sender_id'],
                "sender_name" => $row['sender_name'],
                "sender_university" => $row['sender_university'],
                "receiver_id" => $row['receiver_id'],
                "receiver_name" => $row['receiver_name'],
                "compatibility_score" => (int)$row['compatibility_score'],
                "intro_message" => $row['intro_message'] ?? '',
                "status" => $row['status'],
                "timestamp" => (int)$row['created_at']
            ];
        }
    }

    echo json_encode([
        "status" => "success",
        "data" => [
            "incoming" => $incoming,
            "sent" => $sent
        ]
    ]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
