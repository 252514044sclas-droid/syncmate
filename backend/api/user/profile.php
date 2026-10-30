<?php
require_once __DIR__ . '/../../config/database.php';

// Endpoint to retrieve authenticated user profile
echo json_encode([
    "status" => "success",
    "data" => [
        "id" => "usr_dev_101",
        "name" => "Alex Morgan",
        "email" => "alex.morgan@university.edu",
        "university" => "National Institute of Technology",
        "is_verified" => true,
        "bio" => "Computer Science student looking for summer sublet near campus.",
        "preferred_lease_term" => "Summer 2025",
        "max_budget" => 15000.0,
        "preferred_location" => "Adyar / IIT Campus"
    ]
]);
?>
