<?php
// SubletSync PDO Database Connection Setup
$db_name = "subletsync_db";
$username = "root";
$password = "";

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

$pdo = null;
$ports = [3306, 3307];

foreach ($ports as $port) {
    try {
        $pdo = new PDO("mysql:host=127.0.0.1;port={$port};dbname={$db_name};charset=utf8mb4", $username, $password, [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
        ]);
        if ($pdo) break;
    } catch (PDOException $e) {
        // Try next port if port fails
    }
}
?>
