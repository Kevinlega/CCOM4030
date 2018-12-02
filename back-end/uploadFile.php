<?php
  	
	if(!isset($_REQUEST['path'])) 		exit();


    $file_path = $_REQUEST['path'];
     
    $file_path = $file_path . basename( $_FILES['uploaded_file']['name']);
    if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path) ){
        echo "success";
    } else{
        echo "fail";
    }
 ?>