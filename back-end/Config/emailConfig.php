<?php

/* SEND CUSTOM EMAIL USING USER'S EMAIL ADDRESS */
function sendEmail($to, $subj, $msg)
{
	try
	{ exec("python /var/www/email/sendEmail.py \"$to\" \"$subj\" \"$msg\""); }
	catch (Exception $e) { echo 'Exception: ',  $e->getMessage(), "\n";}
}

//sendEmail("email@somwhere.com", "subject", "content");

//TO SEND SUBSCRIPTION NOTIFICATION
function notifySubs($to)
{
	echo "emailing...";
	try
	{exec("python /var/www/email/notifySubs2.py \"$to\"");}
	catch (Exception $e){ echo 'Exception: ', $e->getMessage(), "\n";}
}


?>
