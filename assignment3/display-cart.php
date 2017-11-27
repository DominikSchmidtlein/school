<?php

session_start();

require_once('includes/art-config.inc.php');
require_once('lib/ArtWorkDB.class.php');
require_once('lib/DatabaseHelper.class.php');

// ensure cart is initialized
if (!isset($_SESSION['cart'])) {
	$_SESSION['cart'] = array();
}

// if user wants to add item to cart
if (isset($_GET['carted-artwork-id'])) {
	// get id of artwork to be added to cart
	$carted_artwork_id = $_GET['carted-artwork-id'];
	if ($carted_artwork_id > 0) {
		$quantity = 0;
		// remove existing entry for art with given id
		foreach ($_SESSION['cart'] as $key=>$item) {
			if ($item['ArtWorkID'] == $carted_artwork_id) {
				$quantity = $item['Quantity'];
				unset($_SESSION['cart'][$key]);
				break;
			}
		}
		// create new entry in cart, with updated quantity
		array_push($_SESSION['cart'], array("ArtWorkID"=>$carted_artwork_id, "Quantity"=>$quantity + 1));
	} else {
		// id set to -1 is used to clear cart
		$_SESSION['cart'] = array();
	}
}

function outputCartRow($file, $product, $quantity, $price) {
   echo '<tr>';
   echo '<td><img class="img-thumbnail" src="images/art/works/tiny/' . $file . '.jpg " alt="..."></td>';
   echo '<td>' . $product . '</td>';
   echo '<td>' . $quantity . '</td>';
   echo '<td>$' . number_format($price,2) . '</td>';
   echo '<td>$' . number_format($quantity * $price,2) . '</td>';
   echo '</tr>';
}

$taxPercent = 0.10;
$shippingThreshold = 2000;
$shippingFlatAmount = 100;

?>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Art Store Cart</title>

	<!-- Bootstrap core CSS -->
	<link href="bootstrap3_defaultTheme/dist/css/bootstrap.css" rel="stylesheet">
	<!-- Custom styles for this template -->
	<link href="bootstrap3_defaultTheme/theme.css" rel="stylesheet">
	<link href="display-cart.css" rel="stylesheet">
</head>

<body>



<?php include 'includes/art-header.inc.php' ?>

<div class="container">

   <div class="page-header">
      <h2>View Cart</h2>
         
      <table class="table table-condensed">
         <tr>
            <th>Image</th>
            <th>Product</th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Amount</th>
         </tr>
         <?php
			$artworkData = new ArtWorkDB();
			
            $subtotal = 0;
			// calculate subtotal and display cart
			foreach ($_SESSION['cart'] as $item) {
				$artWork = $artworkData->findById($item["ArtWorkID"]);
				$subtotal += $item["Quantity"] * $artWork["MSRP"];
				outputCartRow($artWork["ImageFileName"], $artWork["Title"], $item["Quantity"], $artWork["MSRP"]);
			}
            
            // now calculate subtotal, tax, shipping, and grand total
            // $subtotal = ($quantity1 * $price1) + ($quantity2 * $price2);
            $tax = $subtotal * $taxPercent;
            if ($subtotal > $shippingThreshold)
               $shipping = 0;
            else
               $shipping = $shippingFlatAmount;
            $grandTotal = $subtotal + $tax + $shipping;
         
         ?>
         <tr class="success strong">
            <td colspan="4" class="moveRight">Subtotal</td>
            <td>$<?php echo number_format($subtotal,2) ?></td>
         </tr>
         <tr class="active strong">
            <td colspan="4" class="moveRight">Tax</td>
            <td>$<?php echo number_format($tax,2) ?></td>
         </tr>  
         <tr class="strong">
            <td colspan="4" class="moveRight">Shipping</td>
            <td>$<?php echo number_format($shipping,2) ?></td>
         </tr>
         <tr class="warning strong text-danger">
            <td colspan="4" class="moveRight">Grand Total</td>
            <td>$<?php echo number_format($grandTotal,2) ?></td>
         </tr>   
         <tr>
            <td colspan="4" class="moveRight"><button type="button" class="btn btn-primary" >Continue Shopping</button></td>
            <td><a href="display-cart.php?carted-artwork-id=-1" class="btn btn-success" >Checkout</a></td>
         </tr>
      </table>         
         
         

   </div>  <!-- end main row --> 
</div>  <!-- end container -->

<?php include 'includes/art-footer.inc.php' ?>

 <!-- Bootstrap core JavaScript
 ================================================== -->
 <!-- Placed at the end of the document so the pages load faster -->
 <script src="bootstrap3_defaultTheme/assets/js/jquery.js"></script>
 <script src="bootstrap3_defaultTheme/dist/js/bootstrap.min.js"></script>    
</body>
</html>
