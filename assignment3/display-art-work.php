<?php

session_start();

require_once('includes/art-config.inc.php');
require_once('lib/ArtistDB.class.php');
require_once('lib/ArtWorkDB.class.php');
require_once('lib/GenreDB.class.php');
require_once('lib/SubjectDB.class.php');
require_once('lib/GalleryDB.class.php');
require_once('lib/DatabaseHelper.class.php');

// ensure cart is initialized
if (!isset($_SESSION['cart'])) {
	$_SESSION['cart'] = array();
}

if ( isset($_GET['id']) ) {
   $id = $_GET['id'];
}
else {
   // by default, artwork with id 106 is displayed
   $id = 106;   
}

$artworkData = new ArtWorkDB();
// get the requested artwork by id
$artWork = $artworkData->findById($id);
// get all the artwork by the same artist as the requested artwork
$artistWorks = $artworkData->findByArtist($artWork["ArtistID"]);

// get details about artist who created requested art
$artistData = new ArtistDB();
$artist = $artistData->findById($artWork["ArtistID"]);

// get details about which gallery artwork is in
$galleryData = new GalleryDB();
$gallery = $galleryData->findById($artWork["GalleryID"]);

// get genres of artwork
$genreData = new GenreDB();
$genres = $genreData->findByArtWorkId($id);

// get subjects of artwork
$subjectData = new SubjectDB();
$subjects = $subjectData->findByArtWorkId($id);

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
	<!-- Custom stylesheet for add to shopping cart button -->
	<link href="display-art-work.css" rel="stylesheet">
	

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
               <img src="images/art/works/medium/<?php echo $artWork["ImageFileName"] ?>.jpg" class="img-thumbnail img-responsive" alt="<?php echo $artWork["Title"] ?>"/>
            </div>
            <div class="col-md-7">
               <p>
                <?php echo $artWork["Description"] ?>
               </p>
               <p class="price">$<?php echo number_format($artWork["MSRP"], 2) ?></p>
               <div class="btn-group btn-group-lg">
                 <button type="button" class="btn btn-default">
                     <a href="#"><span class="glyphicon glyphicon-gift"></span> Add to Wish List</a>  
                 </button>
				 <a class="btn btn-info" href="display-cart.php?carted-artwork-id=<?php echo $id ?>"><span class="glyphicon glyphicon-shopping-cart"></span> Add to Shopping Cart</a>
               </div>               
               <p>&nbsp;</p>
               <div class="panel panel-default">
                 <div class="panel-heading"><h4>Product Details</h4></div>
                 <table class="table">
                   <tr>
                     <th>Date:</th>
                     <td><?php echo $artWork["YearOfWork"] ?></td>
                   </tr>
                   <tr>
                     <th>Medium:</th>
                     <td><?php echo $artWork["Medium"] ?></td>
                   </tr>  
                   <tr>
                     <th>Dimensions:</th>
                     <td><?php echo $artWork["Width"] ?> cm X <?php echo $artWork["Height"] ?> cm</td>
                   </tr> 
                   <tr>
                     <th>Home:</th>
                     <td><a href="#"><?php echo $gallery["GalleryName"] . ", " . $gallery["GalleryCity"] ?></a></td>
                   </tr>  
                   <tr>
                     <th>Genres:</th>
                     <td><?php echo join(", ", array_map(function($val) { return '<a href="#">' . $val["GenreName"] . '</a>'; }, $genres->fetchAll())) ?></td>
                   </tr> 
                   <tr>
                     <th>Subjects:</th>
                     <td><?php echo join(", ", array_map(function($val) { return '<a href="#">' . $val["SubjectName"] . '</a>'; }, $subjects->fetchAll())) ?></td>
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
