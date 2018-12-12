<?php
// Authors:      Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
//  File: Database.php
//  Description: Database Connection for object models used in web-pages.
//  Created by Los Duendes Malvados.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.

define("DUPLICATE_ENTRY", 1062);
/**
 * Database object. Every model that reads and writes to a database should
 * have this object to interact with the data in the database.
 */
class Database extends mysqli {
	public function __construct() {
		// Get server, username, password from file for database connection.
		$config = parse_ini_file("/var/www/app/config/database.ini");
		parent::__construct($config['server'], $config['username'], '', $config['database']);
		
		if($this->connect_error) {
			echo "Connection failed " . $this->mysqli_error;
		}
	}
}