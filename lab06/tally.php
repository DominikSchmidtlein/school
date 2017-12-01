<?php
	session_start();
	
	if (isset($_POST["vote"])) {
		if ($_POST["vote"] == "yes")
			$_SESSION["yes_votes"] += 1;
		elseif ($_POST["vote"] == "no")
			$_SESSION["no_votes"] += 1;
	}
?>

<html lang="en">
<head>
<title>Lab 06 | Tally</title>
</head>
<body>
<h1>QuickPoll Tally</h1>
Your answer has been registered. The current totals are:<br>
Yes: <?php echo $_SESSION["yes_votes"]; ?><br>
No: <?php echo $_SESSION["no_votes"]; ?><br>
<a href="vote.php">Vote again</a><br>
<a href="register.html">Register a new question</a><br>
</body>
</html>