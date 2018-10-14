<?php
    include_once "config.php";
    $queryType = $_REQUEST["queryType"];

    if($queryType == 0){
        $email = $_REQUEST["email"];
        $password = $_REQUEST["password"];
        $query = "UPDATE users SET hashed_password = '$password' WHERE email = '$email'";
    }

    $result = $connection->query($query);
?>