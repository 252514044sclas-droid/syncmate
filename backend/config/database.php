<?php
// SubletSync Database Connection Setup (MySQL with automatic SQLite fallback)
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
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_TIMEOUT => 2
        ]);
        if ($pdo) break;
    } catch (PDOException $e) {
        // Try next port if port fails
    }
}

// Fallback to SQLite if MySQL is not running
if (!$pdo) {
    try {
        $sqlitePath = __DIR__ . '/../subletsync.sqlite';
        $isNewDb = !file_exists($sqlitePath);
        $pdo = new PDO("sqlite:" . $sqlitePath, null, null, [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
        ]);

        if ($isNewDb) {
            $pdo->exec("
                CREATE TABLE IF NOT EXISTS users (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    university TEXT NOT NULL,
                    is_verified INTEGER DEFAULT 1,
                    profile_picture_url TEXT,
                    bio TEXT,
                    preferred_lease_term TEXT DEFAULT 'Summer 2025',
                    max_budget REAL DEFAULT 15000.00,
                    preferred_location TEXT DEFAULT 'Near Campus / Metro',
                    has_completed_quiz INTEGER DEFAULT 0,
                    quiz_preferences_json TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                CREATE TABLE IF NOT EXISTS listings (
                    id TEXT PRIMARY KEY,
                    owner_id TEXT NOT NULL,
                    title TEXT NOT NULL,
                    address TEXT NOT NULL,
                    price_monthly REAL NOT NULL,
                    bedrooms INTEGER DEFAULT 1,
                    bathrooms REAL DEFAULT 1.0,
                    room_type TEXT DEFAULT 'Private Room',
                    is_furnished INTEGER DEFAULT 1,
                    utilities_included INTEGER DEFAULT 0,
                    duration TEXT DEFAULT 'May 15 - Aug 15, 2025',
                    description TEXT NOT NULL,
                    owner_name TEXT NOT NULL,
                    owner_email TEXT NOT NULL,
                    owner_phone TEXT DEFAULT '',
                    is_verified_student INTEGER DEFAULT 1,
                    distance_to_campus TEXT DEFAULT '0.5 km',
                    risk_score INTEGER DEFAULT 5,
                    predicted_fair_rent REAL DEFAULT 14000.00,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                CREATE TABLE IF NOT EXISTS roommate_requests (
                    request_id TEXT PRIMARY KEY,
                    sender_id TEXT NOT NULL,
                    sender_name TEXT NOT NULL,
                    sender_university TEXT NOT NULL,
                    receiver_id TEXT NOT NULL,
                    receiver_name TEXT NOT NULL,
                    compatibility_score INTEGER DEFAULT 85,
                    intro_message TEXT,
                    status TEXT DEFAULT 'PENDING',
                    created_at BIGINT
                );
            ");
        } else {
            try { $pdo->exec("ALTER TABLE users ADD COLUMN has_completed_quiz INTEGER DEFAULT 0"); } catch (Exception $e) {}
            try { $pdo->exec("ALTER TABLE users ADD COLUMN quiz_preferences_json TEXT"); } catch (Exception $e) {}
            try {
                $pdo->exec("
                    CREATE TABLE IF NOT EXISTS roommate_requests (
                        request_id TEXT PRIMARY KEY,
                        sender_id TEXT NOT NULL,
                        sender_name TEXT NOT NULL,
                        sender_university TEXT NOT NULL,
                        receiver_id TEXT NOT NULL,
                        receiver_name TEXT NOT NULL,
                        compatibility_score INTEGER DEFAULT 85,
                        intro_message TEXT,
                        status TEXT DEFAULT 'PENDING',
                        created_at BIGINT
                    );
                ");
            } catch (Exception $e) {}
        }
    } catch (Exception $e) {
        $pdo = null;
    }
}
?>
