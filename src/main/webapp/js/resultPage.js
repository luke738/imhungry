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
window.localStorage.setItem("searchResults", JSON.stringify(results));
window.localStorage.setItem("imageURLs", JSON.stringify(imageURLs));

console.log(results);

//first check if the restaurant results are empty; if so return empty results msg
if (results[0].length == 0) {
    let restaurant_error = document.createElement("p");
    restaurant_error.innerHTML = "No restaurants within desired radius(mi)";
    restaurant_error.style.color = "red";
    document.querySelector("#restaurantColumn").appendChild(restaurant_error);
}

//implement pagination; seperate for both recipes and restaurants
var rec_list = results[1];
var rest_list = results[0];
var rec_pageList = new Array();
var rest_pageList = new Array();
var currentPage = 1;
var numberPerPage = 5;
var numberOfPages = 0;

function makeList() {
    numberOfRecPages = Math.ceil(rec_list.length / numberPerPage);
    numberOfRestPages = Math.ceil(rest_list.length / numberPerPage);

    //create numbered pg buttons
    numberOfPages = Math.max(numberOfRecPages,numberOfRestPages);
    for (var i = 1; i <= numberOfPages; i++) {
        var input = document.createElement("input");
        input.value = i;
        input.class = "page-link";
        input.type = "button";
        input.id = "page"+i;
        input.addEventListener("click", nextPage);
        document.getElementById("nav").appendChild(input);
    }
}

function nextPage() {
    currentPage += 1;
    loadList();
}

function loadList() {

    document.getElementById("rest_results").innerHTML = "";
    document.getElementById("rec_results").innerHTML = "";

    var begin = ((currentPage - 1) * numberPerPage);
    var end = begin + numberPerPage;

    rec_pageList = rec_list.slice(begin, end);
    rest_pageList = rest_list.slice(begin, end);

    console.log("begin " + begin);
    console.log("end " + end);
    console.log(rec_pageList);
    console.log(rest_pageList);

    var col1 = document.getElementById("column1");
    for(let i = begin; i < end; i++) {
        //Create each sub section for the entry and populate it with data and attributes

        if (results[0][i]!=null) {
            //let sec1 = document.createElement("div");
            //sec1.id = "rest_results";

            let sec1 = document.getElementById("rest_results");

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

    //Same process as above, but for recipe results
    var col2 = document.getElementById("column2");
    for (var i = begin; i < end; i++) {

        if (results[1][i]!=null) {
            // let sec1 = document.createElement("div");
            // sec1.id = "rec_results";

            let sec1 = document.getElementById("rec_results");

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


   // drawList(begin,end,rec_pageList,rest_pageList);
}

function drawList(begin,end) {

        //First, populate restaurant results
        var col1 = document.getElementById("column1");
        for(let i = begin; i < end; i++) {
            //Create each sub section for the entry and populate it with data and attributes
            let sec1 = document.createElement("div");

            // let sec1 = document.getElementById("rest_results");
            // document.getElementById("rest_results").innerHTML = "";

            sec1.setAttribute("class", "Res_section1");
            sec1.innerHTML = results[0][i].name;

            let sec2 = document.createElement("div");
            sec2.setAttribute("class", "Res_section2");
            for(let j = 0; j < 5; j++) {
                if(j < results[0][i].rating) sec2.innerHTML += '⭐';
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
            res.setAttribute("class","item");
            res.setAttribute("id","Res_item"+i);
            //Sets the onclick so that you can navigate to the proper detailed page.
            res.setAttribute("onclick","window.location='restaurantPage.jsp?i="+i+"'");
            res.setAttribute("style","cursor:pointer;");
            res.appendChild(sec1);
            res.appendChild(sec2);
            res.appendChild(divider);
            res.appendChild(sec3);
            res.appendChild(sec4);
            res.appendChild(sec5);

            //Add the entry to the proper place on the page
            col1.appendChild(res);
        }

        //Same process as above, but for recipe results
        var col2 = document.getElementById("column2");
        for (var i = begin; i < end; i++) {
            // let sec1 = document.getElementById("rec_results");
            // document.getElementById("rec_results").innerHTML = "";
            let sec1 = document.createElement("div");
            sec1.setAttribute("class", "Rec_section1");
            sec1.innerHTML = results[1][i].name;

            let sec2 = document.createElement("div");
            sec2.setAttribute("class", "Rec_section2");
            for(let j = 0; j < 5; j++) {
                if(j < results[1][i].rating) sec2.innerHTML += '⭐';
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
            res.setAttribute("class","item");
            res.setAttribute("id","Rec_item" + i);
            res.setAttribute("onclick","window.location='recipePage.jsp?i="+i+"'");
            res.setAttribute("style","cursor:pointer;");
            res.appendChild(sec1);
            res.appendChild(sec2);
            res.appendChild(divider);
            res.appendChild(sec3);
            res.appendChild(sec4);

            col2.appendChild(res);
        }
}

function load() {
    makeList();
    loadList();
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