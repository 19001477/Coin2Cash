<?php
    $con=mysqli_connect("localhost","id17879905_c2cadmin","W!]O%=+2+*TtYBXt","id17879905_dbcoin2cash");

    if ($con) {
        //Assigning JAVA values to local vars
        $email = $_GET['email'];
        $units = $_GET['units'];
        $markers = $_GET['markers'];
        $opt1 = $_GET['opt1'];
        $opt2 = $_GET['opt2'];
        $opt3 = $_GET['opt3'];
        $opt4 = $_GET['opt4'];

        $sql = "UPDATE `tblUsers`
                SET `opt1Setting`='$opt1',`opt2Setting`='$opt2',`opt3Setting`='$opt3',`opt4Setting`='$opt4',`unitsSetting`='$units',`markerSetting`='$markers'
                WHERE email = '$email'";

        //Executing stmt and returning if it worked or not
        if ($con->query($sql) === TRUE) {
            //Inserted successfully
            $response[0]['updated'] = 'true';
        } 
        else {
            //Insert failed
            $response[0]['updated'] = 'false';
            echo "Error: " . $sql . "<br>" . $con->error;
        }

        echo json_encode($response, JSON_PRETTY_PRINT);
    }
    else {
        echo "Database connection failed";
    }
?>