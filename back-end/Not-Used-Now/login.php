<?php
/*
 * THIS FILE RECEIVES EMAIL AND HASHED PASSWORD FROM LOGIN
 * IN ORDER TO VALIDATE USER AS EXISTING AND CORRECT LOGIN INFO.
 * THE FILE THEN SENDS DIRECTIVE (LOGIN ERROR AS 0 OR
 * LOGIN SUCCESSFUL WITH USER ID).
 * FILE BY: BRYAN PESQUERA
 */

//establish database parameters by file inclusion
include_once "config.php";

function dbUserLoginCheck($email, $hashedPass, $connection)
{
	/* SEARCH FOR USER IN DATABASE,
	 * RETURNS USERID IF MATCHES,
	 * ELSE, RETURNS 0.
	 */

	$query = "SELECT user_id FROM users WHERE email=\"$email\" AND hashed_password=\"$hashedPass\"";
	$userId = 0;
	$result = $connection->query($query);
	if($result->num_rows>0)
	{
		while($row = $result->fetch_assoc())
		{
			$userId = $row['user_id'];
			$rows[] = array("id" => $row["user_id"]);
		}
	}
        echo json_encode($rows);
	return $userId;
}

$retVal = 0;
//evaluate login details
if(isset($_REQUEST['email']) && isset($_REQUEST['password']))
{
	$inputEmail = $_REQUEST['email']; //get user's input email
	$inputPass = $_REQUEST['password']; //get user's input password
	$retVal = dbUserLoginCheck($inputEmail, $inputPass, $connection); //check if match
}
else
{
	//USED ONLY FOR TESTING PURPOSES, LEAVE ALL COMMENTED AFTER DEBBUGING
	//$retVal = dbUserLoginCheck("kevin2@gmail.com", "la2", $connection); //should send UID
}

//return userId from database if credentials matched
//else return 0 if credentials did not match
//echo "retVal: ".$retVal."\n";
return $retVal;
?>
