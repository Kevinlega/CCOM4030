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

$RESET_PASSWORD_MESSAGE = "Hello, we want to inform you that your password was just changed, if you did not approve this change, please contact develpers.";

$CHANGE_PASSWD_LINK = "http://54.81.239.120/account/changepassword/";
$CHANGE_PASSWD_MESSAGE = "Please click on the following link to change your account password: " . $CHANGE_PASSWD_LINK;

$VERIFY_LINK = "http://54.81.239.120/account/verify/";
$VERIFY_MESSAGE = "Your account was created, please verify your account at the following link: " . $VERIFY_LINK;

$FILE_UPLOADED_MESSAGE = "Someone uploaded a new file to project: ";

?>
