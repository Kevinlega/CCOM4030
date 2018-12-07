<?php


// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
//  File: fUploadAPI.php
//  Description: File responsible for uploading the files with a given file to a given 
//     		     path.
//
//  Created by Los Duendes Malvados.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.

include_once "config.php";

// For debugging purposes
// error_reporting(E_ALL);
// ini_set('display_errors', TRUE);
// ini_set('display_startup_errors', TRUE);

define("TEXT_FILE",      0);
define("OTHER_FILE",     1);

// Exit if no request variables were passed when called.
if(!isset($_REQUEST['fileType'])) exit();

// Extract file type from URL parameter.
$fileType = $_REQUEST["fileType"];

// Depending on what type of file it is, perform the corresponding code.
switch($fileType){

	   case TEXT_FILE:
	   		if(!isset($_REQUEST['path'])) exit();
			if(!isset($_REQUEST['text'])) exit();

			// Get the path and the text from the URL.
			$text = $_REQUEST["text"];
			$path = $_REQUEST["path"];

			// Attempt to create a file at the given path.
			try{
				$file = fopen("$path", "w") or die ("Unable to open file!");
				fwrite($file, $text);
				fclose($file);
				$return = array("file_created"=>true);
			}
			catch(Exception $e){
				echo 'Couldn\'t create file: ',  $e->getMessage(), "\n"; 
			    $return = array("file_created"=>false);
			}

			break;

	    case OTHER_FILE:

	    	if(!isset($_REQUEST['path'])) exit();

			
			// Create the destination directory for the file from the request
			$target_dir = $_REQUEST['path'] . basename($_FILES["file"]["name"]);

			// Move the file to the destination directory.
			if(move_uploaded_file($_FILES["file"]["tmp_name"], $target_dir)){
				$return = array("file_created"=>true);
			}
			else{
				$return = array("file_created"=>false);				
			}
			break;

	default:
			echo "Invalid parameter";
}

// Display {"file_created": true} if file created successfully.
echo json_encode($return);

?>
