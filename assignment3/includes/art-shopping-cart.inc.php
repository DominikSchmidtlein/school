<div class="panel panel-primary">
   <div class="panel-heading">
      <h3 class="panel-title">Cart </h3>
   </div>
   <div class="panel-body">
   

      
	     <?php
		    $subtotal = 0;
			foreach ($_SESSION['cart'] as $item) {
				$work = $artworkData->findById($item["ArtWorkID"]);
				$subtotal += $item["Quantity"] * $work["MSRP"];
				
				echo '<div class="media">';
				echo '<a class="pull-left" href="#">';
				echo '<img class="media-object" src="images/art/works/tiny/' . $work["ImageFileName"] . '.jpg" alt="..." width="32">';
				echo '</a>';
				echo '<div class="media-body">';
				echo '<p class="cartText"><a href="display-art-work.php?id=' . $work["ArtWorkID"] . '">' . $work["Title"] . '</a></p>';
				echo '</div>';
				echo '</div>';
			}
			echo '<strong class="cartText">Subtotal: <span class="text-warning">$';
			echo $subtotal;
	     ?>
	  </span></strong>
      <div>
      <button type="button" class="btn btn-primary btn-xs"><span class="glyphicon glyphicon-info-sign"></span> Edit</button>
      <a href="display-cart.php?carted-artwork-id=-1" class="btn btn-primary btn-xs"><span class="glyphicon glyphicon-arrow-right"></span> Checkout</a>
      </div>
   </div>
</div>    