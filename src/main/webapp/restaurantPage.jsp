<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Restaurant Page</title>
		<link rel="stylesheet" type="text/css" href="css/detailedPage.css" />
        <link rel="stylesheet" type="text/css" href="css/common.css">
        <link rel="stylesheet" href="css/bootstrap.css">
        <link rel="stylesheet" href="css/bootstrap-responsive.css">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
        <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet">
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.1/css/all.css" integrity="sha384-50oBUHEmvpQ+1lW4y57PTFmhCaXp0ML5d60M1M7uH2+nqUivzIebhndOJK28anvf" crossorigin="anonymous">

	</head>
	<body>
    <script src="js/loginChecker.js"></script>
    <script>checkLoggedIn();</script>>
    <div id="common_header">
        <h4 id = "header_text">I'm Hungry </h4>
    </div>

<div class = "textinfo">
    <p id="title"></p>
    <div class = "address">
        <span id="address1">Address:</span>
        <a href = "" id ="address2"></a>
    </div>
    <div class ="tel">
        <span id="tel1" >Phone number:</span>
        <span id="tel2"></span>
    </div>
    <div class ="website">
        <span id="website1" >Website:</span>
        <a href = "" id ="website2"></a>
    </div>
</div>

<form action = "resultPage.jsp">
    <div class = "backToResults">
        <input type="hidden" id="queryStringInput" name="search" value="" />
        <input type="hidden" id="numberResultsInput" name="number" value="cache" />
        <input type="hidden" id="radiusInput" name="radius" value="" />
        <button type="submit" id = "backtoresults">Back to Results<i class="fas fa-arrow-left"></i> </button>

    </div>
</form>

<form action = "restaurantPagePrint.jsp">
    <div class = "printableVersion">
        <input type="hidden" id="indexInput" name="i" value="">
        <button type="submit" id = "printableversion">Print Page <i class="fas fa-print"></i></button>
    </div>
</form>

<!-- Fairly complicated onclick to add the item, because it not only calls the ListClient function, it updates the back to results button to force the results page to search on the Servlet again -->
<form onsubmit = "addItem(document.getElementById('dropdown').value, result); document.getElementById('numberResultsInput').value = JSON.parse(localStorage.getItem('searchResults'))[0].length; return false;">
    <div class = "addToList">
        <button type="submit" id = "addtolist">Add to List <i class="fas fa-plus"></i></button>

    </div>
</form>

<div class="dropDown">
    <select id = "dropdown">
        <option value="invalid">&nbsp</option>
        <option value="Favorites">Favorites</option>
        <option value="To Explore">To Explore</option>
        <option value="Do Not Show">Do Not Show</option>
    </select>
</div>

<script src="js/ListClient.js"></script>
<script src="js/parseQueryString.js"></script>
<script src="js/restaurantPage.js"></script>
</body>
</html>