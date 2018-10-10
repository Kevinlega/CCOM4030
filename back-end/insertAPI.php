<?php
    include_once "config.php";
    $queryType = $_REQUEST["queryType"];

    if($queryType == 0){
        $name = $_REQUEST["name"];
        $email = $_REQUEST["email"];
        $password = $_REQUEST["password"];
        $salt = $_REQUEST["salt"];
        $query = "INSERT INTO users (name,email,hashed_password,salt) VALUES ('$name','$email','$password','$salt')";
    }

	if($queryType == 1){
		$project_id = (int)$_REQUEST["pid"];
		$user_id = (int)$_REQUEST["uid"];
		$query = "INSERT INTO user_project (project_id,user_id) VALUES ($project_id,$user_id)";
	}
$connection->query($query);
	if($connection->query($query)){
		if($queryType == 0){
			$return = array("registered"=>true);
			echo json_encode($return);
		}
    }
    else{
		if($queryType == 0){
			$return = array("registered"=>false);
			echo json_encode($return);
		}
    }
?>
