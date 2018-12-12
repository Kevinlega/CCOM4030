<?php
// Authors:      Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
//  File: Controller.php
//  Description: Controller Class. All controllers inherit from this base class.
//  Used for web pages, gives basic functionality.
//  Created by Los Duendes Malvados.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.

class Controller {

	/**
	 * Constructor. Starts a new PHP Session.
	 */
	public function __construct() {
		session_start();
	}
	
	/**
	 * Includes a given model and returns a new instance of that model.
	 */
	public function model($model) {
		if(file_exists(ABS_PATH . "models/" . $model . ".php")) {
			require_once(ABS_PATH . "models/" . $model . ".php");
			return new $model;
		}
		echo "Model does not exists.";
	}

	/**
	 * Includes a given view.
	 * @param $data : send data to the view.
	 * @param $withIncludes : load navigation and footer if true.
	 */
	public function view($view = '', $data = [], $withIncludes = true) {
		require_once(ABS_PATH . "views/includes/head.php");

		if($withIncludes) 
			require_once(ABS_PATH . "views/includes/navigation.php");

		if(file_exists(ABS_PATH . "views/" . $view . ".php")) 
			require_once(ABS_PATH . "views/" . $view . ".php");

		if($withIncludes) 
			require_once(ABS_PATH . "views/includes/footer.php");
	}
}
