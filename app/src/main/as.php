<?php
    $con = mysqli_connect("localhost", "id2843947_elbitreats", "password", "id2843947_elbitreats");

    $restoID = (int) $_POST['restaurantid'];
    
    $select = "SELECT * FROM restaurant WHERE restaurantid = ('$restoID')";
    $selectResult = mysqli_query($con, $select);
    
    if (mysqli_num_rows($selectResult) > 0) {
        $resto = mysqli_fetch_assoc($selectResult);
        echo json_encode(array("status"=>"SUCCESS", "resto"=>$resto));
    } else {
        echo json_encode(array("status"=>"EMPTY"));
    }
    
    mysqli_close($con);
?>

<?php
    $con = mysqli_connect("localhost", "id2843947_elbitreats", "password", "id2843947_elbitreats");

    $name = $_POST['name'];
    $restoID = $_POST['restaurantid'];
    $userID = $_POST['userid'];
    
    $insert = "INSERT INTO favorites (restaurant_name, restaurant_id, user_id) VALUES ('$name', $restoID, $userID);";
    header('Content-Type: application/json');
    
    if (mysqli_query($con, $insert)) {
        echo json_encode(array("status"=>"SUCCESS"));
    } else {
        echo json_encode(array("status"=>"FAILED"));
    }
        
    mysqli_close($con);
?>