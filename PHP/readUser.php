<?php
    $con=mysqli_connect("localhost","id17879905_c2cadmin","W!]O%=+2+*TtYBXt","id17879905_dbcoin2cash");
    $response = array();

    if ($con) {
        $email = $_GET['email'];
        $sql = "SELECT * FROM tblUsers where email = '$email'";
        $result = mysqli_query($con, $sql);

        if ($result) {
            $i = 0;
            while ($row = mysqli_fetch_assoc($result)) {
                $response[$i]['email'] = $row['email'];
                $response[$i]['password'] = $row['password'];
                $response[$i]['name'] = $row['name'];
                $response[$i]['surname'] = $row['surname'];
            }
            echo json_encode($response, JSON_PRETTY_PRINT);
        }
    }
    else {
        echo "Database connection failed";
    }
?>