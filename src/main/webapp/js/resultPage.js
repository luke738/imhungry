var query = parseQuery(window.location.search);
//Have to replace '+'s with ' 's before displaying name to user
document.getElementById("header").innerHTML =  query.search.replace(/\+/g, ' ');
var results;
var imageURLs;

//grab previous searches from search page
var prevSearchesJSON = (localStorage.getItem("prevSearches")).trim();
var prevSearches = JSON.parse(prevSearchesJSON);
for (var i = 0; i < prevSearches.length; i++) {
    var jsonData = prevSearches[i];
    console.log(jsonData);
}

//assemble prevSearches collage at "id = prev_search" div
for (var i = 0; i < prevSearches.length;i++) {
    var search = prevSearches[i];
    var search_number = search.expectedResults;
    var search_term = search.searchTerm;
    var search_urls = search.urls;
    var search_radius = search.specifiedRadius;

    //big div is used for column direction so title and collage are vertical but results are still horizontal
    var bigdiv = document.createElement("div");
    bigdiv.id = "bigdiv";
    bigdiv.style.direction = "column";

    //node holds collage
    var node = document.createElement("div");
    node.className = "prev";
    bigdiv.appendChild(node);
    assembleCollage(node,search_urls);

    //title of previous search shown underneath collage, id holds search parameters
    var a = document.createElement("div");
    a.innerHTML = search_term;
    a.id = search_term+ "-" + search_number + "-" + search_radius;
    a.addEventListener("click",go);
    bigdiv.appendChild(a);

    document.getElementById("prev_search").appendChild(bigdiv);
}

//changes url to go to result page with previous search terms
function go() {
    var parameters = (event.srcElement.id).split("-");
    window.location.href = "/resultPage.jsp?search="+parameters[0]+"&number="+parameters[1]+"&radius="+parameters[2];
}

function assembleCollage(append, previmageURLS) {
    for(let i = 0; i < previmageURLS.length; i++) {
        //Create a div to hold this image
        let imgdiv = document.createElement("div");
        imgdiv.setAttribute("class", "imageDiv");
        imgdiv.setAttribute("id", "image"+i);
        //Create the img element
        let img = document.createElement("img");
        img.setAttribute("src", previmageURLS[i]);
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
        append.appendChild(imgdiv);
    }
}

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

//implement pagination, results split
var rec_list = results[1];
var rest_list = results[0];
var numberPerPage = 5;
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
    //remove previously generated recipe items from pg
    var rest_btn = document.getElementById("rest_nav");
    while (rest_btn.firstChild) {
        rest_btn.removeChild(rest_btn.firstChild);
    }

    //remove previously generated recipe items from pg
    var rest_node = document.getElementById("column1");
    while (rest_node.childNodes.length > 2) {
        rest_node.removeChild(rest_node.lastChild);
    }

    //begin = which index in results array to display for specific page.
    //when (value is null) displays first page elements [0 - numberPerPage]
    //end = which index in results array to stop at

    console.log("   REST VALUE" + value);
    var rest_begin = 0;
    if (event.srcElement.value == null) {
        rest_begin = 0;
    } else {
        var id = (event.srcElement.id).split("-");
        var value = parseInt(id[1]);
        rest_begin = ((value - 1) * numberPerPage);
    }
    var rest_end = rest_begin + numberPerPage;

    if (numberOfRestPages > 5) {

        if (value > 2 && (value < numberOfRestPages - 2)) {
            console.log("more than 5 pages + " + value);
            createNumberedRestButton(value - 2, value + 2);
        }
        //if value of button is null, then its the first pg bc value not set yet, so dont show prev button
        if (value != 1 && event.srcElement.id != undefined) { // on all but first pg, show prev button
            createRestButton("Prev", (value-1));
        }
        //create next buttons if not on last page for recipe column
        if (value != numberOfRestPages) { //on all but last pg, show next button
            createRestButton("Next", (value+1));
        }

    } else {
        createNumberedRestButton(1, numberOfRestPages);
        //if value of button is null, then its the first pg bc value not set yet, so dont show prev button
        if (value != 1 && event.srcElement.id != undefined) { // on all but first pg, show prev button
            console.log("else REST first prev " + (value-1));
            createRestButton("Prev", (value-1));
        }
        //create next buttons if not on last page for restaurant column
        if ( (value)<numberOfRestPages) { //on all but last pg, show next button
            console.log("else REST if " + (value+1));
            createRestButton("Next", (value+1));
        }

        if (isNaN(value) && numberOfRestPages != 1) {
            console.log("numberOfRestPages " + numberOfRestPages);
            if (isNaN(value)) {
                createRestButton("Next", (2));
            }
        }

        // make curr clicked page active
        var header = document.getElementById("rest_nav");
        var btns = header.getElementsByTagName("INPUT");

        for (var i = 0; i < btns.length; i++) {

            if (btns[i].id == ("rest-"+value)) {
                console.log(btns[i]);
                // btns[i].className = " active";
                btns[i].style.backgroundColor = "black";
                btns[i].style.color = "white";
            }

        }
    }

    drawRestList(rest_begin, rest_end);
}

//removes items on pg and replaces with newly generated items according to clicked pg value for recipes
function loadRecList() {
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
    var value = parseInt(event.srcElement.id);
    console.log("   REC VALUE" + value);
    var rec_begin = 0;
    if (event.srcElement.value == null) {
        rec_begin = 0;
    } else {
        rec_begin = ((value - 1) * numberPerPage);
    }
    var rec_end = rec_begin + numberPerPage;

    if (numberOfRecPages > 5) {

        if (value > 2 && (value < numberOfRecPages - 2)) {
            console.log("more than 5 pages + " + value);
            createNumberedRecButton(value - 2, value + 2);
        }
        //if value of button is null, then its the first pg bc value not set yet, so dont show prev button
        if (value != 1 && event.srcElement.id != undefined) { // on all but first pg, show prev button
            createRecButton("Prev", (value-1));
        }
        //create next buttons if not on last page for recipe column
        if (value != numberOfRecPages) { //on all but last pg, show next button
            createRecButton("Next", (value+1));
        }

    } else {
        createNumberedRecButton(1, numberOfRecPages);
        //if value of button is null, then its the first pg bc value not set yet, so dont show prev button
        if (value != 1 && event.srcElement.id != undefined) { // on all but first pg, show prev button
            console.log("else first prev " + (value-1));
            createRecButton("Prev", (value-1));
        }
        //create next buttons if not on last page for recipe column
        if ( (value)<numberOfRecPages) { //on all but last pg, show next button
            console.log("else REC if " + (value+1));
            createRecButton("Next", (value+1));
        }

        if (isNaN(value) && numberOfRecPages != 1) {
            console.log("numberOfRecPages " + numberOfRecPages);
            if (isNaN(value)) {
                createRecButton("Next", (2));
            }
        }
        // make curr clicked page active
        var header = document.getElementById("rec_nav");
        var btns = header.getElementsByTagName("INPUT");
        // console.log(btns.length);
        for (var i = 0; i < btns.length; i++) {

            if (btns[i].id == value) {
                console.log(btns[i]);
                // btns[i].className = " active";
                btns[i].style.backgroundColor = "black";
                btns[i].style.color = "white";
            }

        }
    }

    console.log("   rec begin " + rec_begin);
    console.log("   rec end " + rec_end);
    drawRecList(rec_begin, rec_end);
}

//create numbered pg buttons - restaurant
function createNumberedRestButton(start, end) {
    for (var i = start; i <= end; i++) {
        var rest_input = document.createElement("input");
        rest_input.value = i;
        rest_input.class = "page-link";
        rest_input.type = "button";
        rest_input.id = "rest-"+i;
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
        rec_input.id = i;
        rec_input.addEventListener("click", loadRecList);
        document.getElementById("rec_nav").appendChild(rec_input);
    }
}

//creates a button under restaurant column
//insert before the first pg button
function createRestButton(value,number) {
    var input = document.createElement("input");
    input.class = "page-link";
    input.type = "button";
    input.value = value;
    input.id = "rest-"+number;
    input.addEventListener("click", loadRestList);
    if (value == "Prev") {
        document.getElementById("rest_nav").insertBefore(input,document.getElementById("rest-1"));
    } else if (value == "Next") {
        document.getElementById("rest_nav").appendChild(input);
    }
}

//creates a button under recipe column
function createRecButton(value,number) {
    var input = document.createElement("input");
    input.class = "page-link";
    input.type = "button";
    input.value = value;
    input.id = number;
    input.addEventListener("click", loadRecList);
    if (value == "Prev") {
        document.getElementById("rec_nav").insertBefore(input,document.getElementById("1"));
    } else if (value == "Next") {
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
    // if (numberOfRecPages != 1) {
        loadRecList();
    // }
    // if (numberOfRestPages != 1) {
        loadRestList();
    // }
}

window.onload = load;

//prev search iterate through URLS and assemble collage

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