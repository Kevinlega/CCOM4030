<?php

/**
 * Using a given string, set it to the current title of the page.
 */
function set_title($title) {

	$_GET['title'] = $title;
}

/**
 * Return the current page title.
 */
function get_the_title() {

	if(isset($_GET['title']))
		return $_GET['title'];
	else
		return "No Title";
}

/**
 * Print the current page title.
 */
function the_title() {

	echo get_the_title() . " | " . APP_NAME;
}

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
$CHANGE_PASSWD_MESSAGE = "Please click on the following link to change your account password: " . $CHANGE_PASSWD_LINK;

$VERIFY_LINK = "http://54.81.239.120/account/verify/";
$VERIFY_MESSAGE = "Your account was created, please verify your account at the following link: " . $VERIFY_LINK;

?>
