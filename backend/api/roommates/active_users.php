<?php
require_once __DIR__ . '/../../config/database.php';
header('Content-Type: application/json; charset=UTF-8');

try {
    $currentUserId = $_GET['user_id'] ?? '';
    $users = [];

    if ($pdo) {
        $stmt = $pdo->prepare("SELECT id, name, email, university, is_verified, bio, preferred_lease_term, max_budget, preferred_location, has_completed_quiz, quiz_preferences_json FROM users WHERE has_completed_quiz = 1 AND id != ?");
        $stmt->execute([$currentUserId]);
        $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

        foreach ($rows as $r) {
            $prefs = !empty($r['quiz_preferences_json']) ? json_decode($r['quiz_preferences_json'], true) : null;
            $users[] = [
                'id' => $r['id'],
                'name' => $r['name'],
                'email' => $r['email'],
                'university' => $r['university'],
                'is_verified' => (bool)$r['is_verified'],
                'bio' => $r['bio'] ?? '',
                'preferred_lease_term' => $r['preferred_lease_term'] ?? 'Summer 2025',
                'max_budget' => floatval($r['max_budget']),
                'preferred_location' => $r['preferred_location'] ?? 'Near Campus',
                'has_completed_quiz' => true,
                'quiz_preferences' => $prefs
            ];
        }
    }

    echo json_encode(["status" => "success", "data" => $users]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
