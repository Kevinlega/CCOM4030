<?php	


// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
//  File: selectAPI.php
//  Description: Selects data from the data given the appropriate information.
//
//  Created by Los Duendes Malvados.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.

// Used for including the database object.
include_once "config.php";

// Valid queries to execute given the appropriate information.
define("CHECK_IF_USER_EXISTS", 		       0);	// Verify that the given user email exists in the database.
define("USERS_NOT_IN_PROJECT", 		       1);	// Get all of the users that are not in a given project.
define("GET_USER_ID_WITH_EMAIL",           2);	// Given a user's email, get the id of that user in the database.
define("PROJECT_USER_PARTICIPATES",        3);  // Given a user id, get a json of all the projects the user participates in.
define("GET_HASH_AND_SALT", 		       4);	// Displays a json of the hash and salt for the given email in the database.
define("GET_NAME_WITH_EMAIL",              5);  // Displays a json of the name associated with the email address.
define("GET_ALL_FRIENDS_WITH_USER_ID",     6);  // Displays a json of the names and emails of the friends a uid has.
define("GET_PENDING_REQUEST",              7);  // Shows all the pending requests.
define("GET_ADMIN_ID",                     8);  // Given a project id, get the admin(uid) of said project.
define("GET_NAME_WITH_EMAIL_ANDROID",      9);  // Get the name by email and user is not the same as the requestor
define("GET_PROJECT_PATH", 	              10);  // Get the path for that projects.


if(!isset($_REQUEST['queryType'])) exit();

// Extract the query from the URL.
$queryType = $_REQUEST["queryType"];

// Will store the final result to display as a JSON.
$return = array();

// Execute appropriate query.
switch($queryType) {

	case CHECK_IF_USER_EXISTS :
		if(!isset($_REQUEST["email"])) exit();

		// Get variables from the URL.
		$email = $_REQUEST["email"];
		$return = array("registered" => false);

		$query = "SELECT email 
			  FROM users 
			  WHERE email=(?)";

		// Prepare the query statement. (for sanitation)
		$statement = $connection->prepare($query);		
		// Bind the parameters with the sql query.	
		$statement->bind_param('s', $email);	
		// Execute the now sanitized query.			
		$statement->execute();				
		// store the result internally. Used for $statement->num_rows		
		$statement->store_result();					

		if($statement->num_rows == 1)
			$return = array("registered" => true);
		else
			$return = array("registered" => false);

		break;

	case USERS_NOT_IN_PROJECT :
		if(!isset($_REQUEST["pid"]) || !isset($_REQUEST["uid"])) exit();

		// Get variables from the URL.
		$project_id = $_REQUEST["pid"];
		$user_id = $_REQUEST["uid"];
		$emails = array();
		$names = array();

		$query = "SELECT email, name 
			  FROM users 
			  WHERE users.user_id NOT IN (
			  	SELECT user_id 
				FROM user_project 
				WHERE user_project.project_id=(?) ) 
			  AND (users.user_id IN (
				SELECT second_friend 
				FROM friends 
				WHERE friends.first_friend = (?) AND friends.answered=true)
				OR users.user_id IN (
					SELECT first_friend
					FROM friends
					WHERE friends.second_friend = (?) AND friends.answered=true))";
		
		// Prepare the query statement. (for sanitation)
		$statement = $connection->prepare($query);			
		// Bind the parameters with the sql query.
		$statement->bind_param('iii', $project_id,$user_id,$user_id);
		// Execute the now sanitized query.	
		$statement->execute();						
		// Asign the fetch value to this new variable.
		$statement->bind_result($email, $name);				

		while($statement->fetch()) {
			$emails[] = $email;
			$names[] = $name;
		} 
		if(count($names) > 0)
			$return = array("empty" => false,"names" => $names,"emails" => $emails);
		else
			$return = array("empty" => true);

		break;

	case GET_USER_ID_WITH_EMAIL:
		if(!isset($_REQUEST["email"])) exit();

		// Get variables from the URL.
		$email = $_REQUEST["email"];

		$query = "SELECT user_id, verified 
			  FROM users 
			  WHERE users.email = (?)";

		// Prepare the query statement. (for sanitation)
		$statement = $connection->prepare($query);	
		// Bind the parameters with the sql query.		
		$statement->bind_param('s', $email);	
		// Execute the now sanitized query.			
		$statement->execute();						
		// Asign the fetch value to this new variable.
		$statement->bind_result($user_id, $verified);
		// Get the first row.				
		$statement->fetch();						

		$return = array("uid" => $user_id, "verified" => $verified);
		break;

	case PROJECT_USER_PARTICIPATES:
		if(!isset($_REQUEST["uid"])) exit();

		// Get variable from the URL.
		$user_id = $_REQUEST["uid"];
		$name = array();
		$id = array();

		$query = "SELECT name, project_id 
			  FROM projects 
			  WHERE projects.project_id IN (
			  	SELECT project_id 
				FROM user_project 
				WHERE user_project.user_id = (?) )"; 

		// Prepare the query statement. (for sanitation)
		$statement = $connection->prepare($query);		
		// Bind the parameters with the sql query.	
		$statement->bind_param('i', $user_id);	
		// Execute the now sanitized query.			
		$statement->execute();						
		// Asign the fetch value to these new variables.
		$statement->bind_result($project_name, $project_id);	

		while($statement->fetch()) {
			$name[] = "$project_name";
			$id[] = "$project_id";
		}
		
		if(count($id) > 0)
			$return = array("empty" => false,"project_id" => $id,"project_name" => $name);
		else
			$return = array("empty" => true);
		break;

	case GET_HASH_AND_SALT:
		if(!isset($_REQUEST["email"])) exit();
		
		// Get variable from the URL.
		$email = $_REQUEST["email"];

		$query = "SELECT hashed_password, salt 
			  	  FROM users 
			  	  WHERE email=(?)";

		// Prepare the query statement. (for sanitation)
		$statement = $connection->prepare($query);		
		// Bind the parameters with the sql query.		
		$statement->bind_param('s', $email);	
		// Execute the now sanitized query.			
		$statement->execute();						
		// Asign the fetch value to these new variables.
		$statement->bind_result($hashed_password, $salt);
		// Get the results.
		$statement->fetch();

		if(empty($hashed_password))
			$return = array("empty"=>true);
		else
			$return = array("empty"=> false,"hashed_password" => $hashed_password,"salt" => $salt);
		break;

	case GET_NAME_WITH_EMAIL:
		if(!isset($_REQUEST["email"]) || !isset($_REQUEST["uid"])) exit();

		// Get variables from the URL.
		$email = $_REQUEST["email"];
		$user_id = $_REQUEST["uid"];

		$query = "SELECT name 
			  FROM users 
			  WHERE email=(?) AND user_id<>(?)";

		// Prepare the query statement. (for sanitation)
		$statement = $connection->prepare($query);		
		// Bind the parameters with the sql query.	
		$statement->bind_param('si', $email,$user_id);	
		// Execute the now sanitized query.		
		$statement->execute();						
		// Asign the fetch value to these new variables.
		$statement->bind_result($name);	    	  
		// Get the results.          		
		$statement->fetch();

		if(empty($name))
			$empty = true;
		else
			$empty = false;

		$return = array("name" => $name,"empty"=>$empty);

		break;

	case GET_ALL_FRIENDS_WITH_USER_ID:
		if(!isset($_REQUEST["uid"])) exit();

		// Get variables from the URL.
		$user_id = $_REQUEST["uid"];
		$names = array();
		$emails = array();

		$query = "SELECT name, email FROM users 
			  WHERE users.user_id IN (
			  	SELECT second_friend 
				FROM friends 
				WHERE friends.first_friend = (?) AND friends.answered=true) 
				OR users.user_id IN (
					SELECT first_friend
					FROM friends
					WHERE friends.second_friend = (?) AND friends.answered=true)"; 

		// Prepare the query statement. (for sanitation)
		$statement = $connection->prepare($query);			
		// Bind the parameters with the sql query.
		$statement->bind_param('ii', $user_id,$user_id);
		// Execute the now sanitized query.		
		$statement->execute();						
		// Asign the fetch value to these new variables.
		$statement->bind_result($name, $email);				

		while($statement->fetch()) {
			$names[] = $name;
			$emails[] = $email;
		}
		
		if(count($names) > 0)
			$return = array("empty" => false,"name" => $names,"email" => $emails);
		else
			$return = array("empty" => true);
		break;

	case GET_PENDING_REQUEST:
		if(!isset($_REQUEST["uid"])) exit();

		// Get variables from the URL.
		$user_id = $_REQUEST["uid"];
		$name_pending = array();
		$email_pending = array();

		$query = "SELECT name, email FROM users 
			  WHERE users.user_id IN (
			  	SELECT first_friend 
				FROM friends 
				WHERE friends.second_friend = (?) AND friends.answered=false)"; 

		// Prepare the query statement. (for sanitation)
		$statement = $connection->prepare($query);		
		// Bind the parameters with the sql query.	
		$statement->bind_param('i', $user_id);	
		// Execute the now sanitized query.			
		$statement->execute();						
		// Asign the fetch value to these new variables.
		$statement->bind_result($name, $email);				

		while($statement->fetch()) {
			$name_pending[] = $name;
			$email_pending[] = $email;
		}
		
		if(count($name_pending) > 0)
			$return = array("empty" => false,"name" => $name_pending,"email" => $email_pending);
		else
			$return = array("empty" => true);

		break;
	
	case GET_ADMIN_ID:
		if(!isset($_REQUEST["pid"])) exit();

		// Get variables from the URL.
		$project_id = $_REQUEST["pid"];
		$query = "SELECT admin FROM projects WHERE project_id=(?)";

		// Prepare the query statement. (for sanitation)
		$statement = $connection->prepare($query);		
		// Bind the parameters with the sql query.	
		$statement->bind_param('i', $project_id);	
		// Execute the now sanitized query.		
		$statement->execute();			
		// Asign the fetch value to these new variables.			
		$statement->bind_result($admin);
		// Get results.	    	    		
		$statement->fetch();

		$return = array("admin" => $admin);

		break;

	case GET_NAME_WITH_EMAIL_ANDROID:
		if(!isset($_REQUEST["email"]) || !isset($_REQUEST["uid"])) exit();

		$email = $_REQUEST["email"];
		$user_id = $_REQUEST["uid"];

		$query = "SELECT name 
			  FROM users 
			  WHERE email=(?) AND user_id<>(?)";

		// Prepare the query statement. (for sanitation)
		$statement = $connection->prepare($query);
		// Bind the parameters with the sql query.
		$statement->bind_param('si', $email,$user_id);		
		// Execute the now sanitized query.
		$statement->execute();		
		// Asign the fetch value to these new variables.						
		$statement->bind_result($name);	
		// Get the results.    	            
		$statement->fetch();

		if(empty($name))
			$empty = true;
		else
			$empty = false;

		$return = array("name" => [$name],"empty"=>$empty,"email"=>[$email]);

		break;
	case GET_PROJECT_PATH:
		if(!isset($_REQUEST["pid"])) exit();

		// Get variables from the URL.
		$project_id = $_REQUEST["pid"];

		$query = "SELECT folder_link 
			  FROM projects
			  WHERE project_id=(?)";
		
		$statement = $connection->prepare($query);			// Prepare the query statement. (for sanitation)
		$statement->bind_param('i', $project_id);			// Bind the parameters with the sql query.
		$statement->execute();								// Execute the now sanitized query.
		$statement->bind_result($path);	    	            // Asign the fetch value to these new variables.
		$statement->fetch();
		
		if(!empty($project_id))
			$return = array("empty"=>false,"path" => $path);
		else
			$return = array("empty" => true);
		break;

	default :
		echo "Invalid Parameter.";
		exit();
}
$statement->close();
echo json_encode($return);							// Display the result of the query in JSON format.
?>
