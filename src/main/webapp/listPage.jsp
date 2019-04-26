<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>List Management</title>
		<link rel="stylesheet" type="text/css" href="css/listPage.css" />
		<link rel="stylesheet" type="text/css" href="css/common.css">
		<link href="//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
		<link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet">
		<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.1/css/all.css" integrity="sha384-50oBUHEmvpQ+1lW4y57PTFmhCaXp0ML5d60M1M7uH2+nqUivzIebhndOJK28anvf" crossorigin="anonymous">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

	</head>
	<body>
	<script src="js/loginChecker.js"></script>
	<script>checkLoggedIn();</script>
	<div id="common_header">
		<h4 id = "header_text">I'm Hungry </h4>
	</div>
    <form action="listPage.jsp" method="GET">
		<div class="dropDown">
			<select id = "dropdown" name="list">
				<option value="invalid">&nbsp</option>
				<option value="Favorites">Favorites</option>
				<option value="To Explore">To Explore</option>
				<option value="Do Not Show">Do Not Show</option>
				<option id="Grocery" value="Grocery">Grocery</option>
			</select>
		</div>
		<input type="submit" id = "manage_list" value="Manage List" />
	</form>
	
	<form action="resultPage.jsp">
		<input type="hidden" id="queryStringInput" name="search" value="" />
		<input type="hidden" id="numberResultsInput" name="number" value="cache" />
		<input type="hidden" id="radiusInput" name="radius" value="" />
		<input type="submit" id = "back_result" value="Back to Results Page " />
	</form>

	<form action="searchPage.jsp">
		<input type="submit" id = "back_search" value="Back to Search" />
	</form>

	<div id = "header"></div>

	<div id = "container">


	</div>

	<script src="js/ListClient.js"></script>
	<script src="js/parseQueryString.js"></script>
    <script src="js/listPage.js"></script>

	</body>
</html>