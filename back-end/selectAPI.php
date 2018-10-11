<?php
    include_once "config.php";
    $queryType = $_REQUEST["queryType"];

    if($queryType == 0){
        $email = $_REQUEST["email"];
        $query = "SELECT email FROM users WHERE email='$email'";
    }

    elseif($queryType == 1){
        $project_id = $_REQUEST["pid"];
        $query = "SELECT email FROM users WHERE users.user_id NOT IN (SELECT user_id FROM user_project WHERE user_project.project_id= $project_id)";
        $key = "email";
    }
    
    elseif($queryType == 2){
        $email = $_REQUEST["email"];
        $query = "SELECT user_id FROM users WHERE users.email = \"$email\"";
        $key = "user_id";
    }
    elseif ($queryType == 3) {
    	$user_id = $_REQUEST["uid"];
    	$query = "SELECT name FROM projects WHERE projects.project_id IN (SELECT project_id FROM user_project WHERE user_project.user_id = $user_id)"; 	
    	$query2 = "SELECT project_id FROM projects WHERE projects.project_id IN (SELECT project_id FROM user_project WHERE user_project.user_id = $user_id)";
 	}
 	elseif ($queryType == 4) {
 		$email = $_REQUEST["email"];
        $query = "SELECT hashed_password FROM users WHERE email='$email'";
        $query2 = "SELECT salt FROM users WHERE email='$email'";
 	}

    $result = $connection->query($query);
    if($result->num_rows > 0){
        if($queryType == 0){
             $return = array("registered"=>true);
        }
        elseif($queryType == 1 || $queryType == 2){
            $return = array();
            while ($row = $result->fetch_assoc()){
            $return[] = $row[$key];
            }
        }
        elseif ($queryType == 3) {
        	$return = array();
        	$name = array();
        	$id = array();
            while ($row = $result->fetch_assoc()){
            $name[] = $row["name"];
            	}
            $result = $connection->query($query2);
          	while ($row = $result->fetch_assoc()){
            $id[] = $row["project_id"];
            	}
            $return = array("empty"=>"no","project_id"=>$id,"project_name"=>$name);
        	}
        elseif ($queryType == 4) {
        	$return = array();
        	$hashed_password = "";
        	$salt = "";
            while ($row = $result->fetch_assoc()){
            $hashed_password = $row["hashed_password"];
            	}
            $result = $connection->query($query2);
          	while ($row = $result->fetch_assoc()){
            $salt = $row["salt"];
            	}
            $return = array("hashed_password"=>$hashed_password,"salt"=>$salt);
        	}
    }

    else{
        if($queryType == 0){
            $return = array("registered"=>false);
        }
        if($queryType == 3){
        	$return = array("empty"=>"yes");
        }
    }
    echo json_encode($return);
?>