<?php
require_once __DIR__ . '/../../config/database.php';
header('Content-Type: application/json; charset=UTF-8');

$input = json_decode(file_get_contents('php://input'), true) ?? [];
$requestId = $input['request_id'] ?? '';
$newStatus = $input['status'] ?? 'ACCEPTED'; // ACCEPTED or DECLINED

if (!$requestId) {
    echo json_encode(["status" => "error", "message" => "request_id is required"]);
    exit();
}

try {
    if ($pdo) {
        $stmt = $pdo->prepare("UPDATE roommate_requests SET status = ? WHERE request_id = ?");
        $stmt->execute([$newStatus, $requestId]);
    }

    echo json_encode(["status" => "success", "message" => "Request updated to " . $newStatus]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
