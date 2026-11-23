<?php
/**
 * SyncMate Unified PHP Router
 * Handles all API routes, including AI microservice proxies and standard REST endpoints.
 */

$uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);

// Clean prefix (/backend, /api, /backend/api)
$clean = preg_replace('#^/(backend/)?(api/)?#', '', $uri);
$clean = trim($clean, '/');

// Direct route table mapping REST names to PHP files
$routes = [
    'predict_price'           => __DIR__ . '/api/ai/predict_rent.php',
    'predict_rent'            => __DIR__ . '/api/ai/predict_rent.php',
    'predict_compatibility'   => __DIR__ . '/api/ai/predict_compatibility.php',
    'ai/predict_compatibility.php' => __DIR__ . '/api/ai/predict_compatibility.php',
    'ai/predict_rent.php'     => __DIR__ . '/api/ai/predict_rent.php',
    'scan_listing'            => __DIR__ . '/api/ai/scan_listing.php',
    'ai/scan_listing.php'     => __DIR__ . '/api/ai/scan_listing.php',
    'analyze_contract'        => __DIR__ . '/api/ai/analyze_contract.php',
    'ai/analyze_contract.php' => __DIR__ . '/api/ai/analyze_contract.php',
    'chat'                    => __DIR__ . '/api/ai/chat.php',
    'ai/chat.php'             => __DIR__ . '/api/ai/chat.php',
    'roommates/matches.php'   => __DIR__ . '/api/roommates/matches.php',
    'roommates/sync_quiz'     => __DIR__ . '/api/roommates/sync_quiz.php',
    'roommates/active_users'  => __DIR__ . '/api/roommates/active_users.php',
    'roommates/send_request'  => __DIR__ . '/api/roommates/send_request.php',
    'roommates/get_requests'  => __DIR__ . '/api/roommates/get_requests.php',
    'roommates/update_request'=> __DIR__ . '/api/roommates/update_request.php',
    'auth/login.php'          => __DIR__ . '/api/auth/login.php',
    'auth/register.php'       => __DIR__ . '/api/auth/register.php',
    'listings/featured.php'   => __DIR__ . '/api/listings/featured.php',
    'user/profile.php'        => __DIR__ . '/api/user/profile.php',
];

if (isset($routes[$clean]) && file_exists($routes[$clean])) {
    require $routes[$clean];
    exit;
}

// Check direct file under backend/
$directFile = __DIR__ . '/' . ltrim($uri, '/');
if (file_exists($directFile) && !is_dir($directFile)) {
    return false; // Let PHP built-in server serve static files/direct scripts
}

// Check if under backend/api/
$apiFile = __DIR__ . '/api/' . $clean;
if (file_exists($apiFile) && !is_dir($apiFile)) {
    require $apiFile;
    exit;
}

// Fallback
return false;
