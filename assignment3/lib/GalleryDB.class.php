<?php
/*
   Handles database access for the Gallery table. 

 */
class GalleryDB
{  
    private static $baseSQL = "SELECT * FROM galleries";

	// get gallery info given gallery id
	public function findById($id)
	{
		$pdo = DatabaseHelper::setConnectionInfo(array(DBCONNECTION, DBUSER, DBPASS));
        $sql = self::$baseSQL . " WHERE galleries.GalleryID=?";
        $statement = DatabaseHelper::runQuery($pdo, $sql, Array($id));
        return $statement->fetch();
	}

}

?>