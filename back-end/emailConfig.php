<?php

/* SEND CUSTOM EMAIL USING USER'S EMAIL ADDRESS */
function sendEmail($to, $subj, $msg)
{
        echo "emailing..";
        try
	{ exec("python /var/www/email/sendEmail.py \"$to\" \"$subj\" \"$msg\""); }
        catch (Exception $e) { echo 'Exception: ',  $e->getMessage(), "\n";}
}

/* SEND CUSTOM EMAIL USING USER'S ID FROM DATABASE */
/* NOT CURRENTLY WORKING */
/*
function emailI($id, $subj, $msg)
{
        //search database for email from id
        //db query
        exec("python /var/www/email/sendEmail.py \"$id\" \"$subj\" \"$msg\"");
}
*/

//sendEmail("email@somwhere.com", "subject", "content");

//TO SEND SUBSCRIPTION NOTIFICATION
function notifySubs($to)
{
        echo "emailing...";
        try
	{exec("python /var/www/email/notifySubs.py \"$to\"");}
        catch (Exception $e){ echo 'Exception: ', $e->getMessage(), "\n";}
}

?>
