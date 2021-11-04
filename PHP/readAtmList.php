<?php
    $con=mysqli_connect("localhost","id17879905_c2cadmin","W!]O%=+2+*TtYBXt","id17879905_dbcoin2cash");
    $response = array();
    
        if ($con) {
            $sql = "SELECT * FROM tblAtm";
            $result = mysqli_query($con, $sql);
    
            if ($result) {
                $i = 0;
                while ($row = mysqli_fetch_assoc($result)) {
                    $response[$i]['latitude'] = $row['latitude'];
                    $response[$i]['longitude'] = $row['longitude'];
                    $i++;
                }
                echo json_encode($response, JSON_PRETTY_PRINT);
            }
        }
        else {
            echo "Database connection failed";
        }
?>