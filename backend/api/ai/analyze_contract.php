<?php
/**
 * SyncMate PHP Bridge — Contract Guard
 * Forwards the request (including Base64-encoded PDF) to Python Flask at :5000/analyze_contract.
 * Note: Base64 PDFs can be large; timeout is set to 20 seconds.
 */

header('Content-Type: application/json');

$rawInput = file_get_contents('php://input');

// Forward to Python ML microservice
$ch = curl_init('http://127.0.0.1:5000/analyze_contract');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $rawInput);
curl_setopt($ch, CURLOPT_TIMEOUT, 20);

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curlError = curl_error($ch);
curl_close($ch);

if ($httpCode === 200 && $response) {
    http_response_code(200);
    echo $response;
    exit();
}

// Fallback: minimal rule-based analysis on plain contract_text
$input = json_decode($rawInput, true) ?? [];
$fileName     = $input['file_name'] ?? 'contract.pdf';
$contractText = strtolower($input['contract_text'] ?? '');

$flaggedClauses = [];
$riskScore = 0;

$clauses = [
    ['pattern' => 'non-refundable', 'label' => 'Non-Refundable Fee',       'severity' => 'HIGH',   'pts' => 25],
    ['pattern' => 'auto-renew',     'label' => 'Automatic Lease Renewal',  'severity' => 'HIGH',   'pts' => 25],
    ['pattern' => '90-day notice',  'label' => '90-Day Notice Period',     'severity' => 'HIGH',   'pts' => 25],
    ['pattern' => 'cleaning fee',   'label' => 'Mandatory Cleaning Fee',   'severity' => 'MEDIUM', 'pts' => 12],
    ['pattern' => 'no subleas',     'label' => 'Sublease Restriction',     'severity' => 'MEDIUM', 'pts' => 12],
];

foreach ($clauses as $c) {
    if (strpos($contractText, $c['pattern']) !== false) {
        $flaggedClauses[] = [
            'clause_title' => $c['label'],
            'severity'     => $c['severity'],
            'description'  => "Clause detected: \"{$c['pattern']}\"",
            'is_flagged'   => true,
        ];
        $riskScore += $c['pts'];
    }
}
$riskScore = min($riskScore, 100);

echo json_encode([
    'status' => 'success',
    'data'   => [
        'id'                   => uniqid('ctr_', true),
        'file_name'            => $fileName,
        'lease_term'           => '11 months',
        'monthly_rent'         => 0.0,
        'security_deposit'     => 0.0,
        'extra_fees'           => (object)[],
        'utility_responsibilities' => [],
        'notice_period_days'   => 30,
        'sublease_allowed'     => true,
        'flagged_clauses'      => $flaggedClauses,
        'overall_risk_score'   => $riskScore,
        'summary_overview'     => count($flaggedClauses) > 0
            ? count($flaggedClauses) . ' clause(s) flagged. Review carefully before signing.'
            : 'No major red flags detected in this contract.',
        'is_analysis_verified' => false,
        '_fallback'            => true,
        '_error'               => $curlError ?: "Python ML service returned HTTP $httpCode",
    ],
]);
?>
