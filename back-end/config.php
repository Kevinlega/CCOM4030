<?php

// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
//  File: config.php
//  Description: Configuration file. Holds important database 
//   			 connection objects and functions.
//
//  Created by Los Duendes Malvados.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.

$projects_path = "/var/www/html/projects/";

/*
 * ESTABLISH DATABASE CONFIGURATION AND DEFINITIONS
 */

	$host = '127.0.0.1';
	$user = 'root';
	$pass = '';
	$db_name = 'ETNO';

	$connection = new mysqli($host, $user, $pass, $db_name) or die ("Database connection error: %s\n" . $connection -> error);

/*
 * ESTABLISH EMAIL CONFIGURATION AND DEFINITIONS
 */

/* SEND EMAIL USING USER'S EMAIL ADDRESS */
function sendEmail($to, $subj, $msg)
{
        try
	{ exec("python /var/www/email/sendEmail.py \"$to\" \"$subj\" \"$msg\""); }
        catch (Exception $e) { echo 'Exception: ',  $e->getMessage(), "\n";}
}

/* SEND EMAIL USING USER'S ID FROM DATABASE */
function emailI($id, $subj, $msg)
{
        //search database for email from id
        //db query
        exec("python /var/www/email/sendEmail.py \"$id\" \"$subj\" \"$msg\"");
}

//format
//sendEmail("email@somewhere.com", "subject", "content");
?>
