<?php

require_once('includes/art-config.inc.php');
require_once('lib/ArtistDB.class.php');
require_once('lib/ArtWorkDB.class.php');
require_once('lib/DatabaseHelper.class.php');


if ( isset($_GET['id']) ) {
   $id = $_GET['id'];
}
else {
   $id = 106;   
}

$artworkData = new ArtWorkDB();
$artWork = $artworkData->findById($id);

$artistData = new ArtistDB();
$artist = $artistData->findById($artWork["ArtistID"]);

$page = $_SERVER['PHP_SELF'];

?>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Art Store Art</title>

    <!-- Bootstrap core CSS  -->    
    <link href="bootstrap3_defaultTheme/dist/css/bootstrap.css" rel="stylesheet"> 
    <!-- Custom styles for this template -->
    <link href="bootstrap3_defaultTheme/theme.css" rel="stylesheet">

  </head>

  <body>

<?php include 'includes/art-header.inc.php'; ?>

<div class="container">
   <div class="row">

      <div class="col-md-10">
         <h2><?php echo $artWork["Title"] ?></h2>
         <p>By <a href="display-artist.php?id=<?php echo $artist["ArtistID"] ?>"><?php echo $artist["FirstName"] . ' ' .$artist["LastName"]; ?></a></p>
         <div class="row">
            <div class="col-md-5">
               <img src="images/art/works/medium/<?php ?>.jpg" class="img-thumbnail img-responsive" alt="title here"/>
            </div>
            <div class="col-md-7">
               <p>
                description herer
               </p>
               <p class="price">$price</p>
               <div class="btn-group btn-group-lg">
                 <button type="button" class="btn btn-default">
                     <a href="#"><span class="glyphicon glyphicon-gift"></span> Add to Wish List</a>  
                 </button>
                 <button type="button" class="btn btn-default">
                  <a href="#"><span class="glyphicon glyphicon-shopping-cart"></span> Add to Shopping Cart</a>
                 </button>
               </div>               
               <p>&nbsp;</p>
               <div class="panel panel-default">
                 <div class="panel-heading"><h4>Product Details</h4></div>
                 <table class="table">
                   <tr>
                     <th>Date:</th>
                     <td></td>
                   </tr>
                   <tr>
                     <th>Medium:</th>
                     <td></td>
                   </tr>  
                   <tr>
                     <th>Dimensions:</th>
                     <td>? cm X ? cm</td>
                   </tr> 
                   <tr>
                     <th>Home:</th>
                     <td><a href="#"></a></td>
                   </tr>  
                   <tr>
                     <th>Genres:</th>
                     <td>

                     </td>
                   </tr> 
                   <tr>
                     <th>Subjects:</th>
                     <td>
                   
                     </td>
                   </tr>     
                 </table>
               </div>                              
               
            </div>  <!-- end col-md-7 -->
         </div>  <!-- end row (product info) -->

 
         <?php include 'includes/art-artist-works.inc.php'; ?>
                     
      </div>  <!-- end col-md-10 (main content) -->
      
      <div class="col-md-2">   
         <?php include 'includes/art-shopping-cart.inc.php'; ?>
      
         <?php include 'includes/art-right-nav.inc.php'; ?>
      </div> <!-- end col-md-2 (right navigation) -->           
   </div>  <!-- end main row --> 
</div>  <!-- end container -->

<?php include 'includes/art-footer.inc.php'; ?>


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="bootstrap-3.0.0/assets/js/jquery.js"></script>
    <script src="bootstrap-3.0.0/dist/js/bootstrap.min.js"></script>    
  </body>
</html>
