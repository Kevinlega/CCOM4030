<?php
    include_once "config.php";
    $queryType = $_REQUEST["queryType"];

    if($queryType == 0){
        $email = $_REQUEST["email"];
        $password = $_REQUEST["password"];
        $salt = $_REQUEST["salt"];
        $initialValue = $_REQUEST["initialValue"];

        $query = "UPDATE users SET hashed_password = '$password', initialValue = '$initialValue', salt = '$salt'  WHERE email = '$email'";
    }

    $result = $connection->query($query);
?>