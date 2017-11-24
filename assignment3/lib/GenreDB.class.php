<?php
/*
   Handles database access for the Genre table. 

 */
class GenreDB
{  
    private static $baseSQL = "SELECT * FROM genres JOIN artworkgenres ON artworkgenres.GenreID = genres.GenreID";

	public function findByArtWorkId($artworkID)
	{
		$pdo = DatabaseHelper::setConnectionInfo(array(DBCONNECTION, DBUSER, DBPASS));
        $sql = self::$baseSQL . " WHERE artworkgenres.ArtWorkID=?";
        $statement = DatabaseHelper::runQuery($pdo, $sql, Array($artworkID));
        return $statement;
	}

}

?>