var itemIDArray = [];

var totalPrice = 0;

//Run this function when we have loaded the HTML document
window.onload = function () {
	//This code is called when the body element has been loaded and the application starts

	//Request items from the server. The server expects no request body, so we set it to null
	sendRequest("GET", "rest/shop/items", null, function (itemsText) {
		//This code is called when the server has sent its data
		var items = JSON.parse(itemsText);
		addItemsToTable(items);
		addListenerToBuyButton();
//		document.getElementById("message").innerHTML = "Data loaded";

	});

	//Register an event listener for button clicks
	var updateButton = document.getElementById("update");
	addEventListener(updateButton, "click", function () {
		//Same as above, get the items from the server
		sendRequest("GET", "rest/shop/items", null, function (itemsText) {
			//This code is called when the server has sent its data
			var items = JSON.parse(itemsText);
			addItemsToTable(items);
		});
	});

	var createUserButton = document.getElementById("create-account-button");
	addEventListener(createUserButton, "click", function(){
		var username = document.getElementById("customerName").value;
		var password = document.getElementById("customerPass").value;
		var body = "customerName="+ username + "&customerPass=" + password;
		//rest/shop/create?username="+username+"&password=" + password;	
		sendRequest("POST", "rest/shop/create", body, function(answer) {
			document.getElementById("login-message-area").innerHTML=answer;
		});
		//something text field
		alert(body); 	

	});

	var loginButton = document.getElementById("login-button");
	addEventListener(loginButton, "click", function(){
		var username = document.getElementById("customerName").value;
		var password = document.getElementById("customerPass").value;
		var body = "customerName=" + username + "&customerPass=" + password;
		//"rest/shop/login?username="+username+"&password=" + password;	
		sendRequest("POST", "rest/shop/login", body, function(answer){
			document.getElementById("login-message-area").innerHTML=answer;
		});
		alert(url); 
	});
}


function addListenerToBuyButton() {	
	var buyButton = document.getElementById("buy-button");
	buyButton.addEventListener("click", function () {
		document.getElementById("message").innerHTML = ""; // test
		var t = document.getElementById("tabel1");
		var rows = t.getElementsByTagName("TR");
		if (totalPrice == 0) {
			document.getElementById("message").innerHTML = "Your basket is empty!!"; 
		}
		for (var i=0; i<rows.length;i++){
			var cells = rows[i].getElementsByTagName("TD");
			if (parseInt(cells[5].innerHTML) > 0) {
				document.getElementById("message").innerHTML =                          // test
					document.getElementById("message").innerHTML +                      // test
					"ID: " + cells[3].innerHTML + " - " + cells[5].innerHTML + " stk<br/>";           // test

//				Her skal vores request dannes ud fra værdierne i cells[3].innerHTML (ID) og cells[5].innerHTML (inCart)				
			}
		}


//		sendRequest("GET", "rest/shops/buy", null, null);
	});
}

function addItemsToTable(items) {
	//Get the table body we we can add items to it
	var tableBody = document.getElementById("itemtablebody");
	//Remove all contents of the table body (if any exist)
	tableBody.innerHTML = "";
	//Loop through the items from the server
	for (var i = 0; i < items.length; i++) {
		var item = items[i];
		//Create a new line for this item
		var tr = document.createElement("tr");

		var nameCell = document.createElement("td");
		nameCell.textContent = item.name;
		tr.appendChild(nameCell);				

		var priceCell = document.createElement("td");
		priceCell.textContent = item.price;
		tr.appendChild(priceCell);

		var descriptionCell = document.createElement("td");
		descriptionCell.innerHTML = item.description;
		tr.appendChild(descriptionCell);

		var idCell = document.createElement("td");
		idCell.textContent = item.id;
		tr.appendChild(idCell);

		var stockCell = document.createElement("td");
		stockCell.textContent = item.stock;
		tr.appendChild(stockCell);

		var inCartCell = document.createElement("td");
		inCartCell.textContent = "0";
		tr.appendChild(inCartCell);

/////////////////////////////////
		
		var addToCartCell = document.createElement("td");
		var addBtn = document.createElement("BUTTON");        // Create a <button> element
		var addText = document.createTextNode(" + ");       // Create a text node
		addBtn.appendChild(addText);                                // Append the text to <button>
		addToCartCell.appendChild(addBtn); 
		tr.appendChild(addToCartCell); 

		addBtn.addEventListener("click", function()
		{

			document.getElementById("message").innerHTML = "";

			var x = document.activeElement.parentElement.parentElement.rowIndex;
			var t = document.getElementById("tabel1");
			var rows = t.getElementsByTagName("TR");
			var cells = rows[x].getElementsByTagName("TD");
			if (parseInt(cells[4].innerHTML) > 0) {
				cells[5].innerHTML++;
				cells[4].innerHTML--;

				totalPrice +=parseInt(cells[1].innerHTML);

				document.getElementById("total").innerHTML = "Total cost: " + totalPrice + " kr.";
			} else {
				document.getElementById("message").innerHTML = "Sorry! The item is not on storage at the moment";
			}

		});
		
//////////////////////////////////		
		
		var subtFromCartCell = document.createElement("td");
		var subBtn = document.createElement("BUTTON");        // Create a <button> element
		var subText = document.createTextNode(" - ");       // Create a text node
		subBtn.appendChild(subText);                                // Append the text to <button>
		subtFromCartCell.appendChild(subBtn); 
		tr.appendChild(subtFromCartCell); 

		subBtn.addEventListener("click", function()
		{

			document.getElementById("message").innerHTML = "";

			var x = document.activeElement.parentElement.parentElement.rowIndex;
			var t = document.getElementById("tabel1");
			var rows = t.getElementsByTagName("TR");
			var cells = rows[x].getElementsByTagName("TD");
			if (parseInt(cells[5].innerHTML) > 0) {
				cells[5].innerHTML--;
				cells[4].innerHTML++;

				totalPrice -=parseInt(cells[1].innerHTML);

				document.getElementById("total").innerHTML = "Total cost: " + totalPrice + " kr.";
			}

		});

		
		
//////////////////////////////////

		
		tableBody.appendChild(tr);
	}
}


/////////////////////////////////////////////////////
//Code from slides
/////////////////////////////////////////////////////

/**
 * A function that can add event listeners in any browser
 */
function addEventListener(myNode, eventType, myHandlerFunc) {
	if (myNode.addEventListener)
		myNode.addEventListener(eventType, myHandlerFunc, false);
	else
		myNode.attachEvent("on" + eventType,
				function (event) {
			myHandlerFunc.call(myNode, event);
		});
}

var http;
if (!XMLHttpRequest)
	http = new ActiveXObject("Microsoft.XMLHTTP");
else
	http = new XMLHttpRequest();

function sendRequest(httpMethod, url, body, responseHandler) {
	http.open(httpMethod, url);
	if (httpMethod == "POST") {
		http.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	}
	http.onreadystatechange = function () {
		if (http.readyState == 4 && http.status == 200) {
			responseHandler(http.responseText);
		}
	};
	http.send(body);
}
