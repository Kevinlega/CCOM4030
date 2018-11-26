<?php
/*
 * Update API: Changes column values from various tables in the database.
 */

define("UPDATE_PASSWORD", 0);
define("VERIFY_USER",     1);
define("ANSWER_REQUEST",  2);
define("DECLINE_REQUEST", 3);
include_once "config.php";

if(!isset($_REQUEST['queryType'])) exit();
$queryType = $_REQUEST["queryType"];

if($queryType == UPDATE_PASSWORD) {
	if(!isset($_REQUEST['email'])) 		exit();
	if(!isset($_REQUEST['password'])) 	exit();
	if(!isset($_REQUEST['salt'])) 		exit();

	$query = "UPDATE users 
		  SET hashed_password = (?), salt = (?)  
		  WHERE email = (?)";

	$statement = $connection->prepare($query);
	$statement->bind_param('sss', $password, $salt, $email);

	$email = $_REQUEST["email"];
	$password = $_REQUEST["password"];
	$salt = $_REQUEST["salt"];
	$statement->execute();

	if($statement->affected_rows == 1) {
		$return = array("updated" => true);
	} else {
		$return = array("updated" => false);
	}

} else if($queryType == VERIFY_USER) {
	if(!isset($_REQUEST['uid']))	exit();

	$query = "UPDATE users 
		  SET verified=1 
		  WHERE email = (?)";

	$statement = $connection->prepare($query);
	$statement->bind_param('s', $email);

	$email = $_REQUEST["email"];

	$statement->execute();

	if($statement->affected_rows == 1) {
		$return = array("updated" => true);
	} else {
		$return = array("updated" => false);
	}
} else if($queryType == ANSWER_REQUEST) {
	if(!isset($_REQUEST['uid'])) 	exit();
	if(!isset($_REQUEST['email'])) 	exit();

	$query = "UPDATE friends 
		  SET answered = true 
		  WHERE second_friend = (?) AND first_friend = (
			SELECT user_id 
			FROM users 
			WHERE email = (?))";

	$statement = $connection->prepare($query);
	$statement->bind_param('is', $user_id, $email);

	$email = $_REQUEST["email"];
	$user_id = $_REQUEST["uid"];

	$statement->execute();

	if($statement->affected_rows == 1) {
		$return = array("updated" => true);
	} else {
		$return = array("updated" => false);
	}
}else if($queryType == DECLINE_REQUEST) {
	if(!isset($_REQUEST['uid'])) 	exit();
	if(!isset($_REQUEST['email'])) 	exit();

	$query = "DELETE FROM friends 
			WHERE first_friend=(SELECT user_id 
				from users where email=(?)) 
					and second_friend=(?)";

	$statement = $connection->prepare($query);
	$statement->bind_param('si', $email, $user_id);

	$email = $_REQUEST["email"];
	$user_id = $_REQUEST["uid"];

	$statement->execute();

	if($statement->affected_rows == 1) {
		$return = array("updated" => true);
	} else {
		$return = array("updated" => false);
	}
}

echo json_encode($return);
    
?>
