<?php
    $con=mysqli_connect("localhost","id17879905_c2cadmin","W!]O%=+2+*TtYBXt","id17879905_dbcoin2cash");
    $response = array();

    if ($con) {
        $email = $_GET['email'];
        $password = $_GET['password'];
        $sql = "SELECT * FROM tblUsers where email = '$email'";
        $result = mysqli_query($con, $sql);

        if ($result) {
            $row = mysqli_fetch_assoc($result);

            if ($email == $row['email']) {
                if ($password == $row['password']) {
                    $response[0]['login'] = "true";
                }
                else {
                    $response[0]['login'] = "false";
                }
            }
            else {
                $response[0]['login'] = "false";
            }

            echo json_encode($response, JSON_PRETTY_PRINT);
        }
    }
    else {
        echo "Database connection failed";
    }
?>