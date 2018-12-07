<?php

// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
//  File: listdir.php
//  Description: Given a path, list all of the files in the hierarchy.
//  Created by Los Duendes Malvados.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.

// For debugging purposes
// ini_set('display_errors', 1);
// ini_set('display_startup_errors', 1);
// error_reporting(E_ALL);

// No traversing needs to be done if a path is not given.
if(!isset($_REQUEST['path'])) exit();

// Extract the path from the URL.
$path = realpath($_REQUEST['path']);

// Where all of the filenames and type will be stored.
$files = array();

// Recursively traverse through all of the directories at the given path.
foreach (new RecursiveIteratorIterator(new RecursiveDirectoryIterator($path)) as $filename) {

	// Get the name of the file.
	$name = basename($filename);
	if($name != '.' && $name != '..') {
		// Get the directory component of the file.
		$directory = basename(dirname($filename));
		$file = array("filename" => $name, "type" => $directory);
		$files[] = $file;
	}
}

// Displaying the file contents of the path in JSON format.

if(empty($files)) {
	echo json_encode(["empty" => true]);
}
else {
	echo json_encode(["empty"=> false,"files" => $files]);
}

?>
