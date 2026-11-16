<?php
require_once __DIR__ . '/../../config/database.php';

$input = json_decode(file_get_contents('php://input'), true);
$senderId = $input['sender_id'] ?? 'usr_dev_101';
$receiverId = $input['receiver_id'] ?? 'usr_dev_102';
$messageText = $input['message_text'] ?? '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (empty($messageText)) {
        echo json_encode(["status" => "error", "message" => "Message text cannot be empty"]);
        exit();
    }

    try {
        $stmt = $pdo->prepare("INSERT INTO chat_messages (sender_id, receiver_id, message_text) VALUES (?, ?, ?)");
        $stmt->execute([$senderId, $receiverId, $messageText]);

        echo json_encode([
            "status" => "success",
            "message" => "Chat message stored in MySQL successfully",
            "data" => [
                "id" => $pdo->lastInsertId(),
                "sender_id" => $senderId,
                "receiver_id" => $receiverId,
                "message_text" => $messageText
            ]
        ]);
    } catch (PDOException $e) {
        echo json_encode(["status" => "error", "message" => "Database error: " . $e->getMessage()]);
    }
} else {
    // GET request: Fetch chat history
    try {
        $stmt = $pdo->prepare("SELECT * FROM chat_messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY sent_at ASC");
        $stmt->execute([$senderId, $receiverId, $receiverId, $senderId]);
        $messages = $stmt->fetchAll();

        echo json_encode([
            "status" => "success",
            "data" => $messages
        ]);
    } catch (PDOException $e) {
        echo json_encode(["status" => "error", "message" => "Database error: " . $e->getMessage()]);
    }
}
?>
