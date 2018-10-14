<?php	
/**
 * Select API with sanitized input to prevent SQL Injection.
 */
include_once "config.php";

define("CHECK_IF_USER_EXISTS", 		0);	// @param: email -> user email to check in the database.
define("USERS_NOT_IN_PROJECT", 		1);	// @param: pid   -> email of users who are not in a given project.
define("GET_USER_ID_WITH_EMAIL", 	2);	// @param: email -> given a user's email, get the id of that user in the database.
define("PROJECT_USER_PARTICIPATES", 	3);     // @param: uid   -> given a user id, get a json of all the projects the user participates in.
define("GET_HASH_AND_SALT", 		4);	// @param: email -> displays a json of the hash and salt for the given email in the database.

if(isset($_REQUEST['queryType'])) {
	$queryType = $_REQUEST["queryType"];
	$return = array();

	switch($queryType) {
		case CHECK_IF_USER_EXISTS :
			$email = $_REQUEST["email"];
			$return = array("registered" => false);

			$query = "SELECT email 
				  FROM users 
				  WHERE email=(?)";

			$statement = $connection->prepare($query);			// Prepare the query statement. (for sanitation)
			$statement->bind_param('s', $email);				// Bind the parameters with the sql query.
			$statement->execute();						// Execute the now sanitized query.
			$statement->store_result();					// store the result internally. Used for $statement->num_rows

			if($statement->num_rows == 1) {
				$return = array("registered" => true);
			}
			break;

		case USERS_NOT_IN_PROJECT :
			$project_id = $_REQUEST["pid"];

			$query = "SELECT email 
				  FROM users 
				  WHERE users.user_id NOT IN (
				  	SELECT user_id 
					FROM user_project 
					WHERE user_project.project_id=(?) )";
			
			$statement = $connection->prepare($query);			// Prepare the query statement. (for sanitation)
			$statement->bind_param('i', $project_id);			// Bind the parameters with the sql query.
			$statement->execute();						// Execute the now sanitized query.
			$statement->bind_result($email);				// Asign the fetch value to this new variable.

			while($statement->fetch()) {
				$return[] = $email;
			} 
			break;

		case GET_USER_ID_WITH_EMAIL:
			$email = $_REQUEST["email"];

			$query = "SELECT user_id 
				  FROM users 
				  WHERE users.email = (?)";

			$statement = $connection->prepare($query);			// Prepare the query statement. (for sanitation)
			$statement->bind_param('s', $email);				// Bind the parameters with the sql query.
			$statement->execute();						// Execute the now sanitized query.
			$statement->bind_result($user_id);				// Asign the fetch value to this new variable.
			$statement->fetch();						// Get the first row.

			$return = array("$user_id");
			break;

		case PROJECT_USER_PARTICIPATES:
			$user_id = $_REQUEST["uid"];
			$name = array();
			$id = array();

			$query = "SELECT name, project_id 
				  FROM projects 
				  WHERE projects.project_id IN (
				  	SELECT project_id 
					FROM user_project 
					WHERE user_project.user_id = (?) )"; 

			$statement = $connection->prepare($query);			// Prepare the query statement. (for sanitation)
			$statement->bind_param('i', $user_id);				// Bind the parameters with the sql query.
			$statement->execute();						// Execute the now sanitized query.
			$statement->bind_result($project_name, $project_id);		// Asign the fetch value to these new variables.

			while($statement->fetch()) {
				$name[] = "$project_name";
				$id[] = "$project_id";
			}
			
			if(count($id) > 1) {
				$return = array("empty" => "no","project_id" => $id,"project_name" => $name);
			} else {
				$return = array("empty" => "yes");
			}
			break;

		case GET_HASH_AND_SALT:
			$email = $_REQUEST["email"];

			$query = "SELECT hashed_password, salt 
				  FROM users 
				  WHERE email=(?)";

			$statement = $connection->prepare($query);			// Prepare the query statement. (for sanitation)
			$statement->bind_param('s', $email);				// Bind the parameters with the sql query.
			$statement->execute();						// Execute the now sanitized query.
			$statement->bind_result($hashed_password, $salt);	    	// Asign the fetch value to these new variables.
			$statement->fetch();

			$return = array("hashed_password" => $hashed_password,"salt" => $salt);
			break;

		default :
			echo "Invalid Parameter.";
			exit();
		}
		$statement->close();

		echo json_encode($return);						// Display the result of the query in JSON format.
}
?>
