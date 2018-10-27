<?php

/**
 * Insert API: version with sanitized input to prevent SQL injection.
 */
include_once "config.php";

define("CREATE_USER",      0);
define("ADD_USER_PROJECT", 1);
define("CREATE_PROJECT",   2);
define("SEND_REQUEST",     3);

if(isset($_REQUEST['queryType'])) {
	$queryType = $_REQUEST["queryType"];

	switch($queryType) {

		   case CREATE_USER:
		   		$name = $_REQUEST["name"];
				$email = $_REQUEST["email"];
				$password = $_REQUEST["password"];
				$salt = $_REQUEST["salt"];
				$initialValue = $_REQUEST["initialValue"];
				$query = "INSERT INTO users (name,email,hashed_password,salt,initialValue) VALUES (?, ?, ?, ?,?)";

				if(!$statement = $connection->prepare($query)) {
					echo "Prepare failed: (" . $connection->errno . ") " . $connection->error; 
				}
				$statement->bind_param("ssssi", $name, $email, $password, $salt,$initialValue);



				if(!$statement->execute()) {
					$return = array("registered"=>false);
				} else {
					$return = array("registered"=>true);
				}

				break;

		    case ADD_USER_PROJECT:
				$query = "INSERT INTO user_project (project_id,user_id) VALUES (?, ?)";

				if(!$statement = $connection->prepare($query)) {
					echo "Prepare failed: (" . $connection->errno . ") " . $connection->error;
				}
				$statement->bind_param("ii", $project_id, $user_id);

				$project_id = $_REQUEST["pid"];
				$user_id = $_REQUEST["uid"];

				if(!$statement->execute()) {
					$return = array("registered"=>false);
				} else {
					$return = array("registered"=>true);
				}
				break;

		    case CREATE_PROJECT:
				$query = "INSERT INTO projects(name, location, description, folder_link, admin) VALUES(?, ?, ?, ?, ?)";
				if(!$statement = $connection->prepare($query) ) {
					echo "Prepare failed : (" . $connection->errno . ") " . $connection->error;
				}
				$statement->bind_param('ssssi', $name, $location, $description, $folder_link,$user_id);

				$name = $_REQUEST['name'];
				$location = $_REQUEST['location'];
				$description = $_REQUEST['description'];
				$user_id = $_REQUEST['user_id'];
				$folder_link = $_REQUEST['folder_link'];

				if(!$statement->execute()) {
					$return = array("created"=>false);
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
				$statement->bind_param('ii', $user_id, $inserted_project);
				$statement->execute();
				break;

			case SEND_REQUEST:
			    $query = "INSERT INTO friends(first_friend, second_friend, answered) VALUES(?, (SELECT user_id FROM users WHERE email=(?)), false)";

				if(!$statement = $connection->prepare($query) ) {
					echo "Prepare failed : (" . $connection->errno . ") " . $connection->error;
				}
				$statement->bind_param('ss', $first_id, $second_id);

				$first_id = $_REQUEST['uid'];
				$second_id = $_REQUEST['email'];

				if(!$statement->execute()) {
					$return = array("created"=>false);
				} else {
					$return = array("created"=>true);
				}

				break;

		default:
				echo "Invalid parameter";
	}

	$statement->close();
	echo json_encode($return);						// Display the result of the query in JSON format.
}
?>
