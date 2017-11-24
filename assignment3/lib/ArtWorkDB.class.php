<?php
/*
   Handles database access for the ArtWork table. 

 */
class ArtWorkDB 
{  
    private static $baseSQL = "SELECT ArtWorkID, ArtWorks.ArtistID AS ArtistID, FirstName, LastName, Nationality, YearOfBirth, YearOfDeath, Details, ArtistLink, ImageFileName, Title, Description, Excerpt, YearOfWork, Width, Height, Medium, OriginalHome, Cost, MSRP, ArtWorkLink, GoogleLink, FirstName,LastName,Nationality,YearOfBirth, YearOfDeath,Details,ArtistLink
FROM Artists INNER JOIN ArtWorks ON Artists.ArtistID = ArtWorks.ArtistID ";
    
    public function findById($id)
    {
        $pdo = DatabaseHelper::setConnectionInfo(array(DBCONNECTION, DBUSER, DBPASS));
        $sql = self::$baseSQL . " WHERE ArtWorkID=?";
        $statement = DatabaseHelper::runQuery($pdo, $sql, Array($id));
        return $statement->fetch();
        
    }
    
    public function findByArtist($artistID)
    {
        $pdo = DatabaseHelper::setConnectionInfo(array(DBCONNECTION, DBUSER, DBPASS));
        $sql = self::$baseSQL . " WHERE ArtWorks.ArtistID=?";
        $statement = DatabaseHelper::runQuery($pdo, $sql, Array($artistID));
        return $statement;
        
    }    

}

?>