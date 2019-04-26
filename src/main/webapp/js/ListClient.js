//All of these methods put together an AJAX call to ListServlet; this is the only place ListServlet is interacted with.

//Adds the item in item to the list in listName
function addItem(listName, item) {
    var request = {header: "addItem", body: JSON.stringify({header: listName, body: JSON.stringify(item)})};
    console.log(request);
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/Lists", false);
    xhttp.send(JSON.stringify(request));
    console.log("RESP: " + xhttp.response);
    var response = JSON.parse(xhttp.response); //Could check and see if request was successful
}

//Removes the item in item from the list in listName
function removeItem(listName, item) {
    var request = {header: "removeItem", body: JSON.stringify({header: listName, body: JSON.stringify(item)})};
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/Lists", false);
    xhttp.send(JSON.stringify(request));
    var response = JSON.parse(xhttp.response); //Could check and see if request was successful
}

//Requests reordering of recipes/restaurants by
function reorderResults(listName, order, pos){
    // note: "item" only used for order (stored in item.name), must be fully included for parsing purposes in ListServlet.java doPost() method
    var fakeitem = {};
    fakeitem.recipeID = 0;
    fakeitem.prepTime = 0;
    fakeitem.cookTime = 0;
    fakeitem.ingredients = [""];
    fakeitem.instructions = [""];
    fakeitem.imageURL = "";
    fakeitem.name = order;
    fakeitem.pos = pos;
    var request = {header: "reorderList", body: JSON.stringify({header: listName, body: JSON.stringify(fakeitem)})};
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/Lists", false);
    xhttp.send(JSON.stringify(request));
    console.log(xhttp.response);
    var response = JSON.parse(xhttp.response);
}

//Requests reordering of recipes/restaurants by
function groceryCheckbox(item, check){
    // note: "item" only used for order (stored in item.name), must be fully included for parsing purposes in ListServlet.java doPost() method
    var fakeitem = {};
    fakeitem.recipeID = 0;
    fakeitem.prepTime = 0;
    fakeitem.cookTime = 0;
    fakeitem.ingredients = [""];
    fakeitem.instructions = [""];
    fakeitem.imageURL = "";
    fakeitem.name = item;
    fakeitem.pos = 0;
    var checked = "checkGrocery";
    if(check) checked = "uncheckGrocery";
    var request = {header: checked, body: JSON.stringify({header: listName, body: JSON.stringify(fakeitem)})};
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/Lists", false);
    console.log(request);
    xhttp.send(JSON.stringify(request));
    window.location.reload(true);
}

//Invalidates server-side session
function resetLists() { //Debugging function
    var request = {header: "resetLists", body: {header: "null", body: "null"}};
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/Lists", false);
    xhttp.send(JSON.stringify(request));
    var response = JSON.parse(xhttp.response); //Could check and see if request was successful
}

//Gets the list who's name is in listName
function getList(listName) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/Lists?list="+listName, false);
    xhttp.send();
    var response = JSON.parse(xhttp.response);
    console.log("RESP: " + xhttp.response);
    return response;
}