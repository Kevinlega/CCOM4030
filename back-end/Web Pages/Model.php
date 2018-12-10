<?php 

/**
 * Model class: Every Model inherites from this class.
 * Initializes the database object.
 */
class Model {
	
	public $database;

	public function __construct() {
		$this->database = new Database;
	}
}

?>