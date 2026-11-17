<?php
require_once __DIR__ . '/../../config/database.php';
@include_once __DIR__ . '/../../config/ai_keys.php';

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

// Inject Gemini API Key from server config if defined and valid
if (!empty($input['api_key'])) {
    $apiKey = $input['api_key'];
} elseif (defined('GEMINI_API_KEY') && GEMINI_API_KEY !== 'PASTE_YOUR_GEMINI_API_KEY_HERE') {
    $input['api_key'] = GEMINI_API_KEY;
    $rawInput = json_encode($input);
}

// 1. Forward to Python AI Microservice (http://127.0.0.1:5000/chat)
$ch = curl_init('http://127.0.0.1:5000/chat');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $rawInput);
curl_setopt($ch, CURLOPT_TIMEOUT, 12); // 12-second timeout for live LLM inference

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

if ($httpCode === 200 && $response) {
    echo $response;
    exit();
}

// 2. Direct Gemini Fallback if Python microservice is not reachable but Gemini API key is configured
if (defined('GEMINI_API_KEY') && GEMINI_API_KEY !== 'PASTE_YOUR_GEMINI_API_KEY_HERE') {
    $geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" . GEMINI_API_KEY;
    
    $contents = [];
    foreach ($history as $h) {
        $role = ($h['role'] === 'user') ? 'user' : 'model';
        $contents[] = [
            "role" => $role,
            "parts" => [["text" => $h['content']]]
        ];
    }
    $contents[] = [
        "role" => "user",
        "parts" => [["text" => $userMessage]]
    ];

    $payload = [
        "contents" => $contents,
        "systemInstruction" => [
            "parts" => [[
                "text" => "You are SyncMate AI, the intelligent, friendly student housing and subleasing assistant for university students in India. Always use Indian Rupees (₹) for monetary references (e.g. ₹14,500/month rent, ₹15,000 security deposit). Reference Indian cities (Chennai, Bengaluru, Pune, Delhi, Hyderabad). Help students with sublet searches, fair rent benchmarks, lease review, roommate compatibility, and scam safety. Keep answers conversational, helpful, and concise."
            ]]
        ]
    ];

    $gch = curl_init($geminiUrl);
    curl_setopt($gch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($gch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
    curl_setopt($gch, CURLOPT_POST, true);
    curl_setopt($gch, CURLOPT_POSTFIELDS, json_encode($payload));
    curl_setopt($gch, CURLOPT_TIMEOUT, 10);

    $gResponse = curl_exec($gch);
    $gCode = curl_getinfo($gch, CURLINFO_HTTP_CODE);
    curl_close($gch);

    if ($gCode === 200 && $gResponse) {
        $gData = json_decode($gResponse, true);
        $reply = $gData['candidates'][0]['content']['parts'][0]['text'] ?? null;
        if (!empty($reply)) {
            echo json_encode([
                "status" => "success",
                "data" => [
                    "reply" => trim($reply)
                ]
            ]);
            exit();
        }
    }
}

// 3. Dynamic context-aware fallback if all external networks are disconnected
$qLower = strtolower($userMessage);
$reply = "I understand you're asking about " . htmlspecialchars($userMessage) . ". In India, standard student sublets typically range between ₹8,000 to ₹22,000/month depending on the city (e.g., Chennai, Bengaluru, Pune). Make sure to verify the landlord's student ID, insist on a 1-month refundable security deposit in INR (₹), and inspect the property in person before paying any token amount.";

if (strpos($qLower, 'rent') !== false || strpos($qLower, 'price') !== false || strpos($qLower, 'cost') !== false) {
    $reply = "Average student sublet rent currently ranges from ₹9,500/month for a private room in Pune to ₹14,500/month in Adyar, Chennai, and ₹22,000/month in Koramangala, Bengaluru. Use the SyncMate Fair-Rent tool to get an exact ML-powered estimate!";
} elseif (strpos($qLower, 'roommate') !== false || strpos($qLower, 'match') !== false) {
    $reply = "SyncMate's neural roommate matcher analyzes your sleep schedule, cleanliness, study habits, and budget overlap in INR (₹) to pair you with compatible university peers from Anna University, IIT Madras, and DU!";
} elseif (strpos($qLower, 'scam') !== false || strpos($qLower, 'deposit') !== false || strpos($qLower, 'safe') !== false) {
    $reply = "Safety tip: Never transfer advance tokens via wire transfer or UPI before physically viewing the room. Legitimate student hosts accept a standard 1-2 month refundable deposit (e.g. ₹15,000) backed by a written agreement.";
}

echo json_encode([
    "status" => "success",
    "data" => [
        "reply" => $reply
    ]
]);
?>
