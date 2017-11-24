<?php 

?>

<h3>Art by <?php echo utf8_encode($artist["FirstName"]) . ' ' . utf8_encode($artist["LastName"]); ?> </h3>  

<div class="row">
<?php foreach ($artistWorks as $work) { ?>

   <div class="col-md-3">
      <div class="thumbnail">
         <img src="images/art/works/square-medium/<?php echo $work["ImageFileName"]; ?>.jpg" title="<?php echo $work["Title"]; ?>" alt="<?php echo $work["Title"]; ?>" class="img-thumbnail img-responsive">
         <div class="caption">
            <a class="btn btn-primary btn-xs" href="display-art-work.php?id=<?php echo $work["ArtWorkID"] ?>"><span class="glyphicon glyphicon-info-sign"></span> View</a>
            <button type="button" class="btn btn-success btn-xs"><span class="glyphicon glyphicon-gift"></span> Wish</button>
            <button type="button" class="btn btn-info btn-xs"><span class="glyphicon glyphicon-shopping-cart"></span> Cart</button>
         </div>
      </div>                   
   </div>

<?php } ?>
 
            
</div>  <!-- end artist's works row -->