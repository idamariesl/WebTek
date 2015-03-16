var totalPrice = 0;
var loggedIn = false;

//Run this function when we have loaded the HTML document
window.onload = function () {
	//This code is called when the body element has been loaded and the application starts

	//Request items from the server. The server expects no request body, so we set it to null
	sendRequest("GET", "rest/shop/items", null, function (itemsText) {
		//This code is called when the server has sent its data
		var items = JSON.parse(itemsText);
		addItemsToTable(items);
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

	/**
	 * On createUser-button, we add a listener that calls 
	 * the method create in our shopService-class.
	 */
	var createUserButton = document.getElementById("create-account-button");
	addEventListener(createUserButton, "click", function(){
		var username = document.getElementById("customerName").value;
		var password = document.getElementById("customerPass").value;

		if (!(username.length > 0 && password.length > 0)) {
			document.getElementById("login-message-area").innerHTML = "You must fill in both username and password!";
		} else {
			var body = "customerName="+ username + "&customerPass=" + password;
			sendRequest("POST", "rest/shop/create", body, function(answer) {
				document.getElementById("login-message-area").innerHTML=answer;
			});

			document.getElementById("customerName").value = "";
			document.getElementById("customerPass").value = "";

			loggedIn = true;
		}
	});

	/**
	 * On the login-button we add a button we add a 
	 * listener that calls the method login in our shopService-class.
	 */
	var loginButton = document.getElementById("login-button");
	addEventListener(loginButton, "click", function(){
		var username = document.getElementById("customerName").value;
		var password = document.getElementById("customerPass").value;

		if (!(username.length > 0 && password.length > 0)) {
			document.getElementById("login-message-area").innerHTML = "You must fill in both username and password!";
		} else {
			var body = "customerName=" + username + "&customerPass=" + password;
			//"rest/shop/login?username="+username+"&password=" + password;	
			sendRequest("POST", "rest/shop/login", body, function(answer){
				document.getElementById("login-message-area").innerHTML=answer;
			});

			document.getElementById("customerName").value = "";
			document.getElementById("customerPass").value = "";

			//document.getElementById("login-message-area").innerHTML = "Welcome " + username;
			loggedIn = true;
		}
	});


	/**
	 * On the buyButton we add a listener that calls 
	 * the method create in our buy-class, but only if 
	 * the user is logged in.
	 */
	var buyButton = document.getElementById("buy-button");
	addEventListener(buyButton,"click", function () {
		if (loggedIn) {

			document.getElementById("message").innerHTML = ""; // test
			var t = document.getElementById("tabel1");
			var rows = t.getElementsByTagName("TR");
			if (totalPrice == 0) {
				document.getElementById("message").innerHTML = "Your basket is empty!!"; 
			}
			else {
				sendRequest("POST", "rest/shop/sellItems", null, function(saleResponseText) {			
					document.getElementById("message").innerHTML = saleResponseText;		
				});
			}
		}
	});
}

function addItemsToTable(items) {
	totalPrice = 0;

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
		priceCell.style.textAlign = "right";
		tr.appendChild(priceCell);

		var descriptionCell = document.createElement("td");
		descriptionCell.innerHTML = item.description;
		tr.appendChild(descriptionCell);

		var idCell = document.createElement("td");
		idCell.textContent = item.id;
		idCell.style.textAlign = "center";
		tr.appendChild(idCell);

		var stockCell = document.createElement("td");
		stockCell.textContent = item.stock;
		stockCell.style.textAlign = "right";
		tr.appendChild(stockCell);

		var inCartCell = document.createElement("td");
		inCartCell.textContent = "0";
		inCartCell.style.textAlign = "right";
		tr.appendChild(inCartCell);

		/**
		 * Call the addToCart in shopService
		 * with the itemID as a formParam
		 */
		var addToCartCell = document.createElement("td");
		var addBtn = document.createElement("BUTTON");        
		var addText = document.createTextNode(" + ");       
		addBtn.appendChild(addText);                                
		addBtn.addEventListener("click", function() {
			if (loggedIn) {
				document.getElementById("message").innerHTML = "";

				var x = document.activeElement.parentElement.parentElement.rowIndex;
				var t = document.getElementById("tabel1");
				var rows = t.getElementsByTagName("TR");
				var cells = rows[x].getElementsByTagName("TD");
				if (parseInt(cells[4].innerHTML) > 0) {
					cells[5].innerHTML++;
					cells[4].innerHTML--;
					totalPrice +=parseInt(cells[1].innerHTML);
					document.getElementById("total").innerHTML = "&nbsp;Shopping cart: " + totalPrice + " kr.";
					var body = "itemID=" + cells[3].innerHTML;
					sendRequest("POST", "rest/shop/addToCart", body, null);
				} else {
					alert("Sorry! The item is not on storage at the moment");
				}
			} else  {
				alert("Please login before adding to the shopping cart");
			}
				});
		
		/**
		 * Call the subFromCart in shopService
		 * with the itemID as a formParam
		 */
		var subtFromCartCell = document.createElement("td");
		var subBtn = document.createElement("BUTTON");        
		var subText = document.createTextNode(" - ");       
		subBtn.appendChild(subText);                                
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

				document.getElementById("total").innerHTML = "&nbsp;Shopping cart: " + totalPrice + " kr.";
				var body = "itemID=" + cells[3].innerHTML;
				sendRequest("POST", "rest/shop/subFromCart", body, null); 
			}
				});

		addToCartCell.appendChild(addBtn); 
		addToCartCell.appendChild(subBtn); 
		addToCartCell.style.textAlign = "center";
		tr.appendChild(addToCartCell); 

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