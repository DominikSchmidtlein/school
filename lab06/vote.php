<?php
	session_start();
	
	if (isset($_POST["question"])) {
		$_SESSION["question"] = $_POST["question"];
		$_SESSION["yes_votes"] = 0;
		$_SESSION["no_votes"] = 0;
	}
?>

<html lang="en">
<head>
<title>Lab 06 | Vote</title>
</head>
<body>
<h1>QuickPoll Vote</h1>

<?php echo $_SESSION["question"]; ?>?
<form action="tally.php" method="post">
	<input type="radio" name="vote" value="yes"> Yes<br>
	<input type="radio" name="vote" value="no"> No<br>
	<button type="submit">Register my vote</button>
</form>
</body>
</html>