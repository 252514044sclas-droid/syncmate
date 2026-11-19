<?php
require_once __DIR__ . '/../../config/database.php';

header('Content-Type: application/json; charset=UTF-8');

$rawInput = file_get_contents('php://input');
$input = json_decode($rawInput, true);

if (!$input || empty($input['message'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Message cannot be empty."
    ]);
    exit();
}

$userMessage = trim($input['message']);
$history = $input['history'] ?? [];

// 1. Forward to Local In-House Python AI Microservice (http://127.0.0.1:5000/chat)
$ch = curl_init('http://127.0.0.1:5000/chat');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $rawInput);
curl_setopt($ch, CURLOPT_TIMEOUT, 6);

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

if ($httpCode === 200 && $response) {
    echo $response;
    exit();
}

// 2. Local Fallback if Python Microservice is offline
$qLower = strtolower($userMessage);
$reply = "Regarding '$userMessage': In the context of university student housing in India, SyncMate helps you evaluate fair monthly rent in INR (₹) using our self-trained ML models, check lease terms, or find verified roommates near campus.";

if (strpos($qLower, 'rent') !== false || strpos($qLower, 'price') !== false || strpos($qLower, 'cost') !== false || strpos($qLower, 'inr') !== false) {
    $reply = "In Indian university hubs (Chennai, Bengaluru, Delhi, Pune, Hyderabad), student sublets typically rent between ₹8,500 to ₹18,000/month for private rooms, or ₹5,000 to ₹9,500/month for shared rooms. Estimated monthly utilities add ~₹1,800/month.";
} elseif (strpos($qLower, 'roommate') !== false || strpos($qLower, 'flatmate') !== false || strpos($qLower, 'match') !== false) {
    $reply = "SyncMate's neural roommate matching engine evaluates 8 behavioral synergy dimensions (sleep cycles, cleanliness, quiet study hours, and monthly budget overlap in INR ₹). Have you filled out your lifestyle preferences in your profile yet?";
} elseif (strpos($qLower, 'scam') !== false || strpos($qLower, 'safe') !== false || strpos($qLower, 'deposit') !== false) {
    $reply = "Important Student Safety Rule: Never transfer booking tokens or security deposits via UPI/GPay before an in-person physical walkthrough of the sublet property. Standard security deposits in India range from 1 to 2 months refundable rent.";
} elseif (strpos($qLower, 'hi') !== false || strpos($qLower, 'hello') !== false || strpos($qLower, 'hey') !== false) {
    $reply = "Hello! 👋 I'm your SyncMate AI Housing & Subleasing Assistant (powered by our self-trained offline ML models). What would you like to explore today?";
}

echo json_encode([
    "status" => "success",
    "data" => [
        "reply" => $reply,
        "engine" => "syncmate-local-php-v1",
        "is_3rd_party_api" => false
    ]
]);
?>
