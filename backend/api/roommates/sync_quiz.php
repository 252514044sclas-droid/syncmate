<?php
require_once __DIR__ . '/../../config/database.php';
header('Content-Type: application/json; charset=UTF-8');

$input = json_decode(file_get_contents('php://input'), true) ?? [];
$userId = $input['user_id'] ?? '';
$name = $input['name'] ?? 'Student';
$university = $input['university'] ?? 'University';
$preferences = $input['preferences'] ?? [];
$maxBudget = floatval($preferences['max_budget_inr'] ?? $preferences['maxBudgetINR'] ?? $input['max_budget'] ?? 15000);

if (!$userId) {
    echo json_encode(["status" => "error", "message" => "user_id is required"]);
    exit();
}

$prefsJson = json_encode($preferences);

try {
    if ($pdo) {
        // Ensure user row exists
        $stmt = $pdo->prepare("SELECT id FROM users WHERE id = ?");
        $stmt->execute([$userId]);
        if (!$stmt->fetch()) {
            $stmtInsert = $pdo->prepare("INSERT INTO users (id, name, email, password_hash, university, max_budget, has_completed_quiz, quiz_preferences_json) VALUES (?, ?, ?, 'demo', ?, ?, 1, ?)");
            $stmtInsert->execute([$userId, $name, $userId . '@student.edu', $university, $maxBudget, $prefsJson]);
        } else {
            $stmtUpdate = $pdo->prepare("UPDATE users SET name = ?, university = ?, max_budget = ?, has_completed_quiz = 1, quiz_preferences_json = ? WHERE id = ?");
            $stmtUpdate->execute([$name, $university, $maxBudget, $prefsJson, $userId]);
        }
    }

    echo json_encode(["status" => "success", "message" => "Quiz preferences synced successfully"]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
