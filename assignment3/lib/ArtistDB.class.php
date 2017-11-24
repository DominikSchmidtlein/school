<?php
/*
   Handles database access for the Artist table. 

 */
class ArtistDB 
{  

    public function findById($id)
    {
        $pdo = DatabaseHelper::setConnectionInfo(array(DBCONNECTION, DBUSER, DBPASS));
        $statement = DatabaseHelper::runQuery($pdo, "SELECT * FROM Artists WHERE ArtistId=?", Array($id));
        return $statement->fetch();
        
    }

}

?>