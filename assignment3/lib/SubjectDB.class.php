<?php
/*
   Handles database access for the Subject table. 

 */
class SubjectDB
{  
    private static $baseSQL = "SELECT * FROM subjects JOIN artworksubjects ON artworksubjects.SubjectID = subjects.SubjectID";

	// get subjects info given artworkID
	public function findByArtWorkId($artworkID)
	{
		$pdo = DatabaseHelper::setConnectionInfo(array(DBCONNECTION, DBUSER, DBPASS));
        $sql = self::$baseSQL . " WHERE artworksubjects.ArtWorkID=?";
        $statement = DatabaseHelper::runQuery($pdo, $sql, Array($artworkID));
        return $statement;
	}

}

?>