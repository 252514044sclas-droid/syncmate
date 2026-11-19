<?php
/**
 * SyncMate PHP Bridge — Fake-Listing Shield
 * Forwards the request to the Python Flask microservice at :5000/scan_listing
 * and returns the live NLP scam-analysis response to the Android app.
 */

header('Content-Type: application/json');

$rawInput = file_get_contents('php://input');

// Forward to Python ML microservice
$ch = curl_init('http://127.0.0.1:5000/scan_listing');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $rawInput);
curl_setopt($ch, CURLOPT_TIMEOUT, 10);

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curlError = curl_error($ch);
curl_close($ch);

if ($httpCode === 200 && $response) {
    http_response_code(200);
    echo $response;
    exit();
}

// Fallback: Python microservice unavailable
$input = json_decode($rawInput, true) ?? [];
$price = (float)($input['price'] ?? $input['monthly_rent'] ?? 0);
$description = strtolower($input['description'] ?? $input['listing_text'] ?? '');

$riskScore = 0;
$warnSignals = [];

$highPhrases = ['wire transfer', 'western union', 'bitcoin', 'crypto', 'whatsapp only',
                'owner abroad', 'send deposit', 'no viewing', 'advance payment required'];
foreach ($highPhrases as $phrase) {
    if (strpos($description, $phrase) !== false) {
        $warnSignals[] = [
            'signal_type' => 'HIGH',
            'description' => "Scam phrase detected: \"$phrase\"",
            'severity'    => 'HIGH',
        ];
        $riskScore += 25;
    }
}

if ($price > 0 && $price < 4000) {
    $warnSignals[] = [
        'signal_type' => 'HIGH',
        'description' => 'Price ₹' . number_format($price) . '/mo is unrealistically low.',
        'severity'    => 'HIGH',
    ];
    $riskScore += 30;
}

$riskScore = min($riskScore, 100);
$riskLevel = $riskScore >= 70 ? 'HIGH' : ($riskScore >= 35 ? 'MEDIUM' : ($riskScore >= 10 ? 'LOW' : 'SAFE'));

echo json_encode([
    'status' => 'success',
    'data'   => [
        'risk_score'               => $riskScore,
        'risk_level'               => $riskLevel,
        'warning_signals'          => $warnSignals,
        'explanation'              => count($warnSignals) > 0
            ? 'Risk signals found. Never send money before an in-person visit.'
            : 'No major scam signals detected. Still verify before any payment.',
        'image_authenticity_score' => 85,
        'text_authenticity_score'  => max(0, 100 - $riskScore),
        'price_deviation_percent'  => $price > 0 ? round(((12000 - $price) / 12000) * 100, 1) : 0.0,
        'is_verified_backend_service' => false,
        '_fallback'                => true,
        '_error'                   => $curlError ?: "Python ML service returned HTTP $httpCode",
    ],
]);
?>
