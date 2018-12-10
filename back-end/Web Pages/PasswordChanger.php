<?php

/**
 * TODO:
 * This class changes a user account's password
 */
class PasswordChanger extends Model {
	/**
	 * Contains true if the user's password was changed.
	 */
	public $password_was_changed = false;
	/**
	 * Default constructor. Calls the parent constructor, initializing the database.
	 */	
	public function __construct() {
		parent::__construct();
	}
	/**
	 * Hashes the user's password.
	 */	
	public function password_hasher($password, $salt) {
		return md5($password . $salt);
	}
	/**
	 * Changes the user's password.
	 */
	public function change($user, $password) {
		$salt = substr(str_shuffle(str_repeat("0123456789abcdefghijklmnopqrstuvwxyz", 5)), 0, 5);
		$hash = $this->password_hasher($password, $salt);

		$query = "UPDATE users
			  SET hashed_password = (?), salt=(?)
		          WHERE user_id = (?)";

		if(!$statement = $this->database->prepare($query)) {
			echo "Prepare failed: (" . $this->database->errno . ") " . $this->database->error;
		}

		$statement->bind_param("ssi", $hash, $salt, $user);

		if($statement->execute()) {
			$this->password_was_changed = true;
		}
		$statement->close();
	}
}
