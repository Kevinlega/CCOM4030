<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

$path = realpath('/var/www/html/projects');


if(!isset($_REQUEST['path'])) exit();

$path = realpath($_REQUEST['path']);

$files = array();
foreach (new RecursiveIteratorIterator(new RecursiveDirectoryIterator($path)) as $filename)
{
	$name = basename($filename);
	if($name != '.' && $name != '..') {
		$directory = basename(dirname($filename));
		$file = array("filename" => $name, "type" => $directory);
		$files[] = $file;
	}
}
echo json_encode(["files" => $files]);


?>
