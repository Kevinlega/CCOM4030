<?php

// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
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

$RESET_PASSWORD_MESSAGE = "We want to inform you that your password was just changed, if you did not approve this change please contact the developers.";

$CHANGE_PASSWD_LINK = "http://54.81.239.120/account/changepassword/";
$CHANGE_PASSWD_MESSAGE = "A request to change the password of your account was made. Please click on the following link to change your account password: " . $CHANGE_PASSWD_LINK;

$VERIFY_LINK = "http://54.81.239.120/account/verify/";
$VERIFY_MESSAGE = "An account was made with your email address. If this is an error contact the developers of Grafía. If this is correct, please verify your account at the following link: " . $VERIFY_LINK;

$FILE_UPLOADED_MESSAGE = "A new file was uploaded to project: ";
$ADDED_PROJECT = "A friend just added you to his project. You can now contribute his project.";

?>
