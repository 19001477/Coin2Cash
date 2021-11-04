<?php
    $con=mysqli_connect("localhost","id17879905_c2cadmin","W!]O%=+2+*TtYBXt","id17879905_dbcoin2cash");

    if ($con) {
        //Assigning JAVA values to local vars
        $email = $_GET['email'];
        $password = $_GET['password'];
        $name = $_GET['name'];
        $surname = $_GET['surname'];

        $sql = "INSERT INTO `tblUsers`(`email`, `password`, `name`, `surname`) 
                VALUES ('$email','$password','$name','$surname')";

        //Executing stmt and returning if it worked or not
        if ($con->query($sql) === TRUE) {
            //Inserted successfully
            $response[0]['inserted'] = 'true';
        } 
        else {
            //Insert failed
            $response[0]['inserted'] = 'false';
            echo "Error: " . $sql . "<br>" . $con->error;
        }

        echo json_encode($response, JSON_PRETTY_PRINT);
    }
    else {
        echo "Database connection failed";
    }
?>