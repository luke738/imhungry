var query = parseQuery(window.location.search);
//Have to replace '+'s with ' 's before displaying name to user
document.getElementById("header").innerHTML =  query.search.replace(/\+/g, ' ');
var results;
var imageURLs;
//To reduce server overhead and improve performance, the page will only search from the server if it was arrived at from the search page
//or if a list was modified on the last page. Otherwise, it'll load the results from localStorage (much faster).
if(query.number == "cache") {
    results = JSON.parse(localStorage.getItem("searchResults"));
    imageURLs = JSON.parse(localStorage.getItem("imageURLs"));
}
else {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/Search?search=" + query.search + "&number=" + query.number + "&radius=" + query.radius + "&userID=" + localStorage.getItem("userID"), false);
    xhttp.send();
    console.log(xhttp.response);
    var response = JSON.parse(xhttp.response);
    results = response.body.results;
    imageURLs = response.body.imageURLs;
}
//Store results in local storage
window.localStorage.setItem("search", query.search);
window.localStorage.setItem("radius", query.radius);
window.localStorage.setItem("searchResults", JSON.stringify(results));
window.localStorage.setItem("imageURLs", JSON.stringify(imageURLs));

//first check if the restaurant results are empty; if so return empty results msg
if (results[0].length == 0) {
    let restaurant_error = document.createElement("p");
    restaurant_error.innerHTML = "No restaurants within desired radius(mi)";
    restaurant_error.style.color = "red";
    document.querySelector("#restaurantColumn").appendChild(restaurant_error);
}

//first check if the restaurant results are empty; if so return empty results msg
if (results[1].length == 0) {
    let recipe_error = document.createElement("p");
    recipe_error.innerHTML = "No recipes based on search";
    recipe_error.style.color = "red";
    document.querySelector("#recipeColumn").appendChild(recipe_error);
}

//implement pagination, results split into pageLists
var rec_list = results[1];
var rest_list = results[0];
var rec_pageList = new Array();
var rest_pageList = new Array();
var numberPerPage = 5;
var numberOfPages = 0;
var numberOfRecPages = 0;
var numberOfRestPages = 0;

//creates numbered pg buttons according to the max of either recipe or restaurant results
function makeList() {
    numberOfRecPages = Math.ceil(rec_list.length / numberPerPage);
    numberOfRestPages = Math.ceil(rest_list.length / numberPerPage);

    //show up to first five buttons
    if (numberOfRestPages >= 5) {
        createNumberedRestButton(1, 5);
    } else {
        createNumberedRestButton(1, numberOfRestPages);
    }
    if (numberOfRecPages >= 5) {
        createNumberedRecButton(1, 5);
    } else {
        createNumberedRecButton(1, numberOfRestPages);
    }
}

//removes items on pg and replaces with newly generated items according to clicked pg value for restaurants
function loadRestList() {
    //clear buttons and regenerate them for each page so it doesnt build
    var rest_btn = document.getElementById("rest_nav");
    while (rest_btn.firstChild) {
        rest_btn.removeChild(rest_btn.firstChild);
    }

    //remove previously generated restaurant items on pg so results dont build on each other
    var rest_node = document.getElementById("column1");
    while (rest_node.childNodes.length > 2) {
        rest_node.removeChild(rest_node.lastChild);
    }

    //begin = which index in results array to display for specific page.
    //when (value is null) displays first page elements [0 - numberPerPage]
    //end = which index in results array to stop at
    var rest_begin = 0;
    if (event.srcElement.value == null) {
        rest_begin = 0;
    } else {
        rest_begin = ((event.srcElement.value - 1) * numberPerPage);
    }
    var rest_end = rest_begin + numberPerPage;

    //if clicked btn value is not undefined (undefined = no button has been clicked yet- displays just numbered btns)
    if (event.srcElement.value != undefined) {

        createNumberedRestButton(event.srcElement.value, numberOfRestPages);

        //create prev buttons if not on first page for restaurant column
        if (event.srcElement.value != 1) { // show prev
            createRestButton("Prev");
        }

        //create next buttons if not on last page for restaurant column
        if (event.srcElement.value != numberOfRestPages) { //show
            createRestButton("Next");
        }
    }

    drawRestList(rest_begin, rest_end);
}

//removes items on pg and replaces with newly generated items according to clicked pg value for recipes
function loadRecList() {
    console.log("in load Rec list ");
    //remove previously generated recipe items from pg
    var rec_btn = document.getElementById("rec_nav");
    while (rec_btn.firstChild) {
        rec_btn.removeChild(rec_btn.firstChild);
    }

    //remove previously generated recipe items from pg
    var rec_node = document.getElementById("column2");
    while (rec_node.childNodes.length > 2) {
        rec_node.removeChild(rec_node.lastChild);
    }

    //begin = which index in results array to display for specific page.
    //when (value is null) displays first page elements [0 - numberPerPage]
    //end = which index in results array to stop at
    var rec_begin = 0;
    if (event.srcElement.value == null) {
        rec_begin = 0;
    } else {
        rec_begin = ((event.srcElement.value - 1) * numberPerPage);
    }
    var rec_end = rec_begin + numberPerPage;

    if (numberOfRecPages > 5) {
        console.log("if > 5" + numberOfRecPages);

        if (event.srcElement.value > 2 && (event.srcElement.value < numberOfRecPages - 2)) {
            createNumberedRecButton(event.srcElement.value - 2, event.srcElement.value + 2);
        }
        //if value of button is null, then its the first pg bc value not set yet, so dont show prev button
        if (event.srcElement.value != 1) { // on all but first pg, show prev button
            createRecButton("Prev");
        }
        //create next buttons if not on last page for recipe column
        if (event.srcElement.value != numberOfRecPages) { //on all but last pg, show next button
            createRecButton("Next");
        }
    } else {
        console.log("else");
        createNumberedRecButton(1, numberOfRecPages);
        //prev next
        //if value of button is null, then its the first pg bc value not set yet, so dont show prev button
        if (event.srcElement.value != 1) { // on all but first pg, show prev button
            createRecButton("Prev");
        }
        //create next buttons if not on last page for recipe column
        if (event.srcElement.value != numberOfRecPages) { //on all but last pg, show next button
            createRecButton("Next");
        }
    }
    drawRecList(rec_begin, rec_end);
}

//create numbered pg buttons - restaurant
function createNumberedRestButton(start, end) {
    for (var i = start; i <= end; i++) {
        var rest_input = document.createElement("input");
        rest_input.value = i;
        rest_input.class = "page-link";
        rest_input.type = "button";
        rest_input.id = "rest_page";
        rest_input.addEventListener("click", loadRestList);
        document.getElementById("rest_nav").appendChild(rest_input);
    }
}

//create numbered pg buttons - recipe
function createNumberedRecButton(start, end) {
    for (var i = start; i <= end; i++) {
        var rec_input = document.createElement("input");
        rec_input.value = i;
        rec_input.class = "page-link";
        rec_input.type = "button";
        rec_input.id = "rec_page";
        rec_input.addEventListener("click", loadRecList);
        document.getElementById("rec_nav").appendChild(rec_input);
    }
}

//creates a button under restaurant column
//insert before the first pg button
function createRestButton(value) {
    var input = document.createElement("input");
    input.class = "page-link";
    input.type = "button";
    input.value = value;
    input.id = "rest_page";
    input.addEventListener("click", loadRestList);
    if (value == "prev") {
        document.getElementById("rest_nav").insertBefore(input,document.getElementById("page1"));
    } else if (value == "next") {
        document.getElementById("rec_nav").appendChild(input);
    }
}

//creates a button under recipe column
function createRecButton(value) {
    var input = document.createElement("input");
    input.class = "page-link";
    input.type = "button";
    input.value = value;
    input.id = "rec_page";
    input.addEventListener("click", loadRecList);
    if (value == "prev") {
        document.getElementById("rec_nav").insertBefore(input,document.getElementById("page1"));
    } else if (value == "next") {
        document.getElementById("rec_nav").appendChild(input);
    }
}

//generates the list of item results to show
function drawRestList(begin,end) {

    var col1 = document.getElementById("column1");
    for(let i = begin; i < end; i++) {
        //Create each sub section for the entry and populate it with data and attributes

        if (results[0][i]!=null) {
            let sec1 = document.createElement("div");
            sec1.class = "rest_results";

            sec1.setAttribute("class", "Res_section1");
            sec1.innerHTML = results[0][i].name;

            let sec2 = document.createElement("div");
            sec2.setAttribute("class", "Res_section2");
            for (let j = 0; j < 5; j++) {
                if (j < results[0][i].rating) sec2.innerHTML += '⭐';
                else sec2.innerHTML += '☆';
            }

            let divider = document.createElement("div");
            divider.setAttribute("class", "divider");

            let sec3 = document.createElement("div");
            sec3.setAttribute("class", "Res_section3");
            sec3.innerHTML = results[0][i].driveTimeText + " away";

            let sec4 = document.createElement("div");
            sec4.setAttribute("class", "Res_section4");
            sec4.innerHTML = results[0][i].address;

            let sec5 = document.createElement("div");
            sec5.setAttribute("class", "Res_section5");
            sec5.innerHTML = results[0][i].priceLevel;

            //Create the actual entry element and set the previous subsections to be its children
            let res = document.createElement("div");
            res.setAttribute("class", "item");
            res.setAttribute("id", "Res_item" + i);
            //Sets the onclick so that you can navigate to the proper detailed page.
            res.setAttribute("onclick", "window.location='restaurantPage.jsp?i=" + i + "'");
            res.setAttribute("style", "cursor:pointer;");
            res.appendChild(sec1);
            res.appendChild(sec2);
            res.appendChild(divider);
            res.appendChild(sec3);
            res.appendChild(sec4);
            res.appendChild(sec5);

            //Add the entry to the proper place on the page
            col1.appendChild(res);
        }
    }
}

function drawRecList(begin,end) {
    //Same process as above, but for recipe results
    var col2 = document.getElementById("column2");
    for (var i = begin; i < end; i++) {

        if (results[1][i]!=null) {
            let sec1 = document.createElement("div");
            sec1.class = "rec_results";

            sec1.setAttribute("class", "Rec_section1");
            sec1.innerHTML = results[1][i].name;

            let sec2 = document.createElement("div");
            sec2.setAttribute("class", "Rec_section2");
            for (let j = 0; j < 5; j++) {
                if (j < results[1][i].rating) sec2.innerHTML += '⭐';
                else sec2.innerHTML += '☆';
            }

            let divider = document.createElement("div");
            divider.setAttribute("class", "divider");

            let sec3 = document.createElement("div");
            sec3.setAttribute("class", "Rec_section3");
            sec3.innerHTML = results[1][i].prepTime + " min prep time";

            let sec4 = document.createElement("div");
            sec4.setAttribute("class", "Rec_section4");
            sec4.innerHTML = results[1][i].cookTime + " min cook time";

            let res = document.createElement("div");
            res.setAttribute("class", "item");
            res.setAttribute("id", "Rec_item" + i);
            res.setAttribute("onclick", "window.location='recipePage.jsp?i=" + i + "'");
            res.setAttribute("style", "cursor:pointer;");
            res.appendChild(sec1);
            res.appendChild(sec2);
            res.appendChild(divider);
            res.appendChild(sec3);
            res.appendChild(sec4);

            col2.appendChild(res);
        }
    }
}

function load() {
    makeList();
    loadRecList();
    loadRestList();
}

window.onload = load;

//Assemble the collage
var collage = document.getElementById("collage");
for(let i = 0; i < imageURLs.length; i++) {
    //Create a div to hold this image
    let imgdiv = document.createElement("div");
    imgdiv.setAttribute("class", "imageDiv");
    imgdiv.setAttribute("id", "image"+i);
    //Create the img element
    let img = document.createElement("img");
    img.setAttribute("src", imageURLs[i]);
    img.setAttribute("class", "image");
    //Add the img to the div
    imgdiv.appendChild(img);
    //Generate a set of randomized position, rotation angle, scaling factor, and z index
    let x = 2*(i%5-1)*20+Math.floor(Math.random()*30);
    let y = 2*(i%2)*50+Math.floor(Math.random()*30)-20;
    let rot = Math.floor(Math.random()*90)-45;
    let scale = Math.random()*0.2+0.9;
    let z = Math.floor(Math.random()*50);
    //Apply a style to the element that applies the above transformations to it
    imgdiv.setAttribute("style", "-webkit-transform: translate("+x+"%, "+y+"%) rotate("+rot+"deg) scale("+scale+");" +
        "-ms-transform: translate("+x+"%, "+y+"%) rotate("+rot+"deg) scale("+scale+");" +
        "transform: translate("+x+"%, "+y+"%) rotate("+rot+"deg) scale("+scale+");" +
        "z-index:"+z+";");
    //Add the element to the collage
    collage.appendChild(imgdiv);
}