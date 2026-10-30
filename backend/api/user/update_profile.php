<?php
require_once __DIR__ . '/../../config/database.php';

$input = json_decode(file_get_contents('php://input'), true);

$name = $input['name'] ?? 'Alex Morgan';
$university = $input['university'] ?? 'National Institute of Technology';
$bio = $input['bio'] ?? '';
$preferredLeaseTerm = $input['preferred_lease_term'] ?? 'Summer 2025';
$maxBudget = $input['max_budget'] ?? 15000.0;
$preferredLocation = $input['preferred_location'] ?? 'Adyar / IIT Campus';

echo json_encode([
    "status" => "success",
    "message" => "Profile updated successfully",
    "data" => [
        "id" => "usr_dev_101",
        "name" => $name,
        "email" => "alex.morgan@university.edu",
        "university" => $university,
        "is_verified" => true,
        "bio" => $bio,
        "preferred_lease_term" => $preferredLeaseTerm,
        "max_budget" => (float)$maxBudget,
        "preferred_location" => $preferredLocation
    ]
]);
?>
