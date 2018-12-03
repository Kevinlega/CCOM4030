<?php

/**
 * Insert API: version with sanitized input to prevent SQL injection.
 */
include_once "config.php";

error_reporting(E_ALL);
ini_set('display_errors', TRUE);
ini_set('display_startup_errors', TRUE);

define("TEXT_FILE",      0);
define("IMAGE_FILE",     1);
define("VIDEO_FILE",     2);
define("AUDIO_FILE",     3);

if(!isset($_REQUEST['fileType'])) exit();

$fileType = $_REQUEST["fileType"];
switch($fileType){

	   case TEXT_FILE:
	   		if(!isset($_REQUEST['path'])) exit();
			if(!isset($_REQUEST['text'])) exit();

			$text = $_REQUEST["text"];
			$path = $_REQUEST["path"];
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

	    case IMAGE_FILE:

	    	if(!isset($_REQUEST['path'])) exit();

			// Por si hay que hacer un directorio
			// $target_dir = "media";

			// if(!file_exists($target_dir)){
			// 	mkdir($target_dir, 0777, true);
			// }


						

			$target_dir = $_REQUEST['path'] . basename($_FILES["file"]["name"]);
			if(move_uploaded_file($_FILES["file"]["tmp_name"], $target_dir)){
				$return = array("file_created"=>true);
			}
			else{
				$return = array("file_created"=>false);				
			}
			break;

	    case VIDEO_FILE:
			break;

		case AUDIO_FILE:
			break;

	default:
			echo "Invalid parameter";
}

echo json_encode($return);						// Display the result of the query in JSON format.

?>
