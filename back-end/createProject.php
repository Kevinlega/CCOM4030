<?php
        include_once("config.php");

        if(isset($_REQUEST['queryType'])) {
                $queryType = $_REQUEST['queryType'];

                if($queryType == 2) {

                        $name = $_REQUEST['name'];
                        $location = $_REQUEST['location'];
                        $description = $_REQUEST['description'];
                        $user_id = $_REQUEST['user_id'];
                        $folder_link = $_REQUEST['folder_link'];

                        // Create the new project.
                        $query = "INSERT INTO projects(name, location, description, folder_link, user_id) VALUES(?, ?, ?, ?, ?)";

                        if(!$statement = $connection->prepare($query) ) {
                                echo "Prepare failed : (" . $connection->errno . ") " . $connection->error;
                        }

                        $statement->bind_param('ssssi', $name, $location, $description, $folder_link,$user_id);

                        if(!$statement->execute()) {
                                echo "Execution failed : (" . $connection->errno . ") " . $connection->error;
                        }

                        $inserted_project = $statement->insert_id;

                        $statement->close();

                        // Create User Project Relation
                        $query = "INSERT INTO user_project(user_id, project_id) VALUES(?, ?)";

                        if(!$statement = $connection->prepare($query)) {
                                echo "Prepare failed : (" . $connection->errno . ") " . $connection->error;
                        }
                        $statement->bind_param('ii', $user_id, $inserted_project);

                        if(!$statement->execute()) {
                                echo "Execution failed : (" . $connection->errno . ") " . $connection->error;
                        } else {
                                $return = array("created"=>true);
                                echo json_encode($return);
                        }

                }
        }
?>