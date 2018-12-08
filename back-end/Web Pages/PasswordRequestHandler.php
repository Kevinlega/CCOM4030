<?php

/**
 * TODO:
 * This class manages password change requests.
 */
class PasswordRequestHandler extends Model {
	/**
	 * Default constructor. Calls the parent constructor, initializing the database.
	 */	
	public function __construct() {
		parent::__construct();
	}
	/**
	 * Checks if request is valid.
	 */
	public function is_legit_request($request) {

		$query = "SELECT * 
			  FROM reset_password_requests
		          WHERE request_id = (\"$request\")";

		if(!$response = $this->database->query($query)) {
			echo "No Response";
		}
		
		if($response->num_rows) {
			return true;
		}
		
		return false;
	}
	/**
	 * Deletes a given request.
	 */
	public function delete_request($request) {
		$query = "DELETE FROM reset_password_requests
			  WHERE request_id='{$request}'";
		return $this->database->query($query);
	}
}
