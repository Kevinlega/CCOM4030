<?php

// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
//  File: updateAPI.php
//  Description: Changes column values from various tables in the database.
//
//  Created by Los Duendes Malvados.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.

// Used for including the database object.
include_once "config.php";

define("UPDATE_PASSWORD", 0);	// Change a user's password.
define("VERIFY_USER",     1);	// Given a user_id, mark them as a verified user.
define("ANSWER_REQUEST",  2);	// Answer a request as accepted.
define("DECLINE_REQUEST", 3);	// Answer a request as declined.
define("RESEND_VERIFY",   4);   // Resend verification

// Nothing needs to be executed if query type is not given.
if(!isset($_REQUEST['queryType'])) exit();

// Get the query from the URL.
$queryType = $_REQUEST["queryType"];

if($queryType == UPDATE_PASSWORD) {
	if(!isset($_REQUEST['email'])) 		exit();
	if(!isset($_REQUEST['password'])) 	exit();
	if(!isset($_REQUEST['salt'])) 		exit();

	$query = "UPDATE users 
		  SET hashed_password = (?), salt = (?)  
		  WHERE email = (?)";

	// Prepare the query for execution.
	$statement = $connection->prepare($query);
	// Bind the given variables to the prepared query.
	$statement->bind_param('sss', $password, $salt, $email);

	// Get the variables from the URL.
	$email = $_REQUEST["email"];
	$password = $_REQUEST["password"];
	$salt = $_REQUEST["salt"];
	$statement->execute();

	if($statement->affected_rows == 1) {
		$return = array("updated" => true);
		sendEmail($email, "Your Password was Changed", $RESET_PASSWORD_MESSAGE);
	} else {
		$return = array("updated" => false);
	}

} else if($queryType == VERIFY_USER) {
	if(!isset($_REQUEST['email']))	exit();

	$query = "UPDATE users 
		  SET verified=1 
		  WHERE email = (?)";

	// Prepare the query for execution.
	$statement = $connection->prepare($query);
	// Bind the variables to the prepared query.
	$statement->bind_param('s', $email);

	// Get variable from URL.
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

	// Prepare the query for execution.
	$statement = $connection->prepare($query);
	// Bind the given variables to query.
	$statement->bind_param('is', $user_id, $email);

	// Get variables from URL.
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

	// Prepare the query for execution.
	$statement = $connection->prepare($query);
	// Bind the variables given to the query.
	$statement->bind_param('si', $email, $user_id);

	// Get variables from URL.
	$email = $_REQUEST["email"];
	$user_id = $_REQUEST["uid"];

	$statement->execute();

	if($statement->affected_rows == 1) {
		$return = array("updated" => true);
	} else {
		$return = array("updated" => false);
	}
} else if($queryType == RESEND_VERIFY) {
	if(!isset($_REQUEST['email'])) 	exit();

	$query = "SELECT verified FROM users WHERE email=?";

	// Prepare the query for execution.
	$statement = $connection->prepare($query);
	// Bind the variables given to the query.
	$statement->bind_param('s', $email);

	// Get variables from URL.
	$email = $_REQUEST["email"];
	$statement->execute();

	$statement->bind_result($verified);	 // Asign the fetch value to these new variables.
	$statement->fetch();
	
	if($verified == 0)
		// send email aqui
		$return = array("updated"=>true);
	else
		$return = array("updated" => false);
} 

// Display the contents from the results of query requested as a JSON string.
echo json_encode($return);
    
?>
