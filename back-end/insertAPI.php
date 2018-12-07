<?php

// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
//  File: insertAPI.php
//  Description: Responsible for inserting data into database.
//
//  Created by Los Duendes Malvados.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.


// Includes the database object.
include_once "config.php";

// For debugging purposes
// error_reporting(E_ALL);
// ini_set('display_errors', TRUE);
// ini_set('display_startup_errors', TRUE);

// Valid query types that can be executed given the valid information.
define("CREATE_USER",      0);		// Creates a new user given the name, email, password and salt
define("ADD_USER_PROJECT", 1);		// Inserts a user to project relation given the project id and user email.
define("CREATE_PROJECT",   2);		// Creates a new project with the name, description and location.
define("SEND_REQUEST",     3);		// Inserts the friend requests for the users. Needs two (2) user_ids.
define("RESET_PASSWD",     4);		// Adds a reset password requests to database given a user_id.

// Nothing need be done if no query specified.
if(!isset($_REQUEST['queryType'])) exit();

$queryType = $_REQUEST["queryType"];

switch($queryType) {

	   case CREATE_USER:

	   		// Get variables from url.
	   		$name = $_REQUEST["name"];
			$email = $_REQUEST["email"];
			$password = $_REQUEST["password"];
			$salt = $_REQUEST["salt"];

			$query = "INSERT INTO users (name,email,hashed_password,salt) VALUES (?, ?, ?,?)";

			// Prepare the query for execution.
			if(!$statement = $connection->prepare($query)) {
				echo "Prepare failed: (" . $connection->errno . ") " . $connection->error; 
			}
			// Bind given variables to the prepared query.
			$statement->bind_param("ssss", $name, $email, $password, $salt);

			if(!$statement->execute()) {
				$return = array("registered"=>false);
			} else {
				$return = array("registered"=>true);
			}

			break;

	    case ADD_USER_PROJECT:
			$query = "INSERT INTO user_project (project_id,user_id) VALUES (?, (SELECT user_id FROM users WHERE email = (?)))";

			// Prepare the query for execution.
			if(!$statement = $connection->prepare($query)) {
				echo "Prepare failed: (" . $connection->errno . ") " . $connection->error;
			}
			// Bind given variables to the prepared query.
			$statement->bind_param("is", $project_id, $email);

			// Get variables from url.
			$project_id = $_REQUEST["pid"];
			$email = $_REQUEST["email"];

			if(!$statement->execute()) {
				$return = array("registered"=>false);
			} else {
				$return = array("registered"=>true);
			}
			break;

	    case CREATE_PROJECT:
			$query = "INSERT INTO projects(name, location, description, folder_link, admin) VALUES(?, ?, ?, ?, ?)";

			// Prepare the query for execution.
			if(!$statement = $connection->prepare($query) ) {
				echo "Prepare failed : (" . $connection->errno . ") " . $connection->error;
			}
			// Bind given variables to the prepared query.
			$statement->bind_param('ssssi', $name, $location, $description, $folder_link, $user_id);

			// Get variables from url.
			$name = $_REQUEST['name'];
			$location = $_REQUEST['location'];
			$description = $_REQUEST['description'];
			$user_id = $_REQUEST['user_id'];

			$cmd = "python /var/www/new_dir.py {$user_id} 2>&1";
			$json = json_decode(shell_exec($cmd), true);

			$folder_link = $json['folder_link'];
			if(!$statement->execute()) {
				$return = array("created"=>false);
				echo $connection->error;
				break;
			} else {
				$inserted_project = $statement->insert_id;
				$return = array("created"=>true,"project_id"=>$inserted_project);
			}
			
			$statement->close();

			// Create User Project Relation
			$query = "INSERT INTO user_project(user_id, project_id) VALUES(?, ?)";

			if(!$statement = $connection->prepare($query)) {
				echo "Prepare failed : (" . $connection->errno . ") " . $connection->error;
			}
			// Bind given variables to the prepared query.
			$statement->bind_param('ii', $user_id, $inserted_project);
			$statement->execute();
			break;

		case SEND_REQUEST:
		    	$query = "INSERT INTO friends(first_friend, second_friend, answered) VALUES(?, (SELECT user_id FROM users WHERE email=(?)), false)";

			if(!$statement = $connection->prepare($query) ) {
				echo "Prepare failed : (" . $connection->errno . ") " . $connection->error;
			}
			// Bind given variables to the prepared query.
			$statement->bind_param('is', $first_id, $second_id);

			// Get variables from url.
			$first_id = $_REQUEST['uid'];
			$second_id = $_REQUEST['email'];

			if(!$statement->execute()) {
				$return = array("created"=>false);
			} else {
				$return = array("created"=>true);
			}

			break;
		case RESET_PASSWD:
			if(!isset($_REQUEST['uid'])) exit();
			
			$query = "INSERT INTO reset_password_requests(user_id, request_id)
				  VALUES(?, ?)";

			// Prepare the query for execution.
			$statement = $connection->prepare($query);
			// Bind given variables to the prepared query.
			$statement->bind_param('is', $user_id, $request_id);
			
			// Get variables from url.
			$user_id = $_REQUEST['uid'];
			$request_id = md5(uniqid(rand(), true));
			
			if($statement->execute()) {
				$return = array("inserted" => true);
			} else {
				$return = array("inserted" => false);
			}
			break;

	default:
			echo "Invalid parameter";
}

$statement->close();

// Display the result of the query in JSON format.
echo json_encode($return);

?>