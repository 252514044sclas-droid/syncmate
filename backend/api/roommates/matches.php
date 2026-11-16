<?php
require_once __DIR__ . '/../../config/database.php';

header('Content-Type: application/json; charset=UTF-8');

// 1. Fetch prospective roommate profiles from database if available
$candidates = [];
if (isset($pdo)) {
    try {
        $stmt = $pdo->query("SELECT id, name, university, avatar_url, compatibility_percentage, match_reason, lifestyle_rhythm, max_budget_monthly, preferred_location, bio FROM roommates ORDER BY compatibility_percentage DESC LIMIT 10");
        $dbCandidates = $stmt->fetchAll(PDO::FETCH_ASSOC);
        if ($dbCandidates && count($dbCandidates) > 0) {
            foreach ($dbCandidates as $c) {
                $candidates[] = [
                    "id" => $c['id'],
                    "name" => $c['name'],
                    "university" => $c['university'],
                    "avatar_url" => $c['avatar_url'],
                    "lifestyle_rhythm" => $c['lifestyle_rhythm'],
                    "max_budget_monthly" => (float)$c['max_budget_monthly'],
                    "preferred_location" => $c['preferred_location'],
                    "bio" => $c['bio'] ?? ""
                ];
            }
        }
    } catch (Exception $e) {
        // Fall through to default candidates
    }
}

// 2. Query Python Neural Matcher Microservice (:5000/match_roommates)
$ch = curl_init('http://127.0.0.1:5000/match_roommates');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
curl_setopt($ch, CURLOPT_POST, true);

$payload = [
    "user_budget" => 15000.0,
    "user_sleep" => 0.2,
    "user_clean" => 0.8,
    "user_noise" => 0.2,
    "user_smoker" => 0,
    "user_location" => "Adyar, Chennai",
    "user_university" => "Anna University"
];
if (!empty($candidates)) {
    $payload['candidates'] = $candidates;
}

curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));
curl_setopt($ch, CURLOPT_TIMEOUT, 3);

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

if ($httpCode === 200 && $response) {
    // Return live Python MLP neural compatibility matches
    echo $response;
    exit();
}

// 3. Fallback response if Python microservice is offline
echo json_encode([
    "status" => "success",
    "data" => [
        [
            "id" => "rm_1",
            "name" => "Maya Lin",
            "university" => "Anna University '26",
            "avatar_url" => null,
            "compatibility_percentage" => 96,
            "match_reason" => "Matches your quiet study hours & budget",
            "lifestyle_rhythm" => "Early Riser • Clean & Quiet",
            "max_budget_monthly" => 12000.0,
            "preferred_location" => "Adyar / Guindy",
            "is_saved" => false,
            "bio" => "CS junior looking for a clean, peaceful sublet room for summer internship."
        ],
        [
            "id" => "rm_2",
            "name" => "Jordan Patel",
            "university" => "IIT Madras '25",
            "avatar_url" => null,
            "compatibility_percentage" => 91,
            "match_reason" => "Similar lease term & campus quadrant",
            "lifestyle_rhythm" => "Night Owl • Quiet & Organized",
            "max_budget_monthly" => 16500.0,
            "preferred_location" => "Koramangala, Bengaluru",
            "is_saved" => false,
            "bio" => "Grad student in ECE. Friendly, respectful, and loves cooking on weekends."
        ],
        [
            "id" => "rm_3",
            "name" => "Chloe Bennett",
            "university" => "Delhi University '25",
            "avatar_url" => null,
            "compatibility_percentage" => 88,
            "match_reason" => "Shared campus commute & budget preference",
            "lifestyle_rhythm" => "Flexible • Social & Clean",
            "max_budget_monthly" => 14000.0,
            "preferred_location" => "North Campus, Delhi",
            "is_saved" => false,
            "bio" => "Commerce major looking for a sublet roommate near North Campus."
        ]
    ]
]);
?>

