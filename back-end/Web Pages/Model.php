<?php 
// Authors:      Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
//  File: Model.php
//  Description: Model Class. Every Model inherites from this class. Initializes the database object.
//  Created by Los Duendes Malvados.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.

class Model {
	
	public $database;
	public function __construct() {
		$this->database = new Database;
	}
}
?>