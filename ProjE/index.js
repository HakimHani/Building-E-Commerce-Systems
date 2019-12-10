//Notes:
//ask what port number to use.
//ask what they means by omitted parameter: then update relative cards like cart
//1) /list    2) /list?id=
//update home
//update database path
//for accepting external http requests: app.use((req, res, next) => { res.header("Access-Control-Allow-Origin", "*"); next(); });
//update host path: currently it's /static
//update where static folder is: html, css and js

//for building app.use(/) service:
//get the parameter from the request then parse it if it's of type JSON object
//set headers in responce including 200 and returned payload type:'Content-Type': 'application/json'

//to delete one item from array: .splice(index, 1);
//to add one item to array at given index: .splice(index, 0, new_item);
//to add to the end of the array: .push(new_item);

//check varibale type by using: typeof then you can print it to console
//console.log("type of object.qty " + typeof object.qty);


const port = 8000;
const home = '/home/hakim';
const DB_PATH = home + '/4413Go/hr4413/pkg/sqlite/Models_R_US.db';

const net = require('net');
const https = require('https');
const express = require('express');
const session = require('express-session');
var fs = require('fs');

var app = express();
var sqlite3 = require('sqlite3').verbose();
var db = new sqlite3.Database(DB_PATH);
app.enable('trust proxy');


var localCart;
app.use(session({
	secret: "mine",
	proxy: true,
	resave: true,
	saveUninitialized: true
}));

app.use('/static', express.static(home + "/www/static"));

app.use("/list", (req, res) => {
	res.header('Access-Control-Allow-Origin', '*');
	let id = req.query.id;
	let query = "select id, name from product where catid = ?";
	db.all(query, [id], (err, rows) => {
		if (err == null) {
			res.writeHead(200, {
				'Content-Type': 'application/json'
			});
			res.write(JSON.stringify(rows));
			res.end();
		} else {
			res.end("Error " + err);
		}
	});
});

// an alternative working list node route
// app.use('/list', function (req, res) {
// 	let id = req.query.id;
// 	res.writeHead(200, {'Content-Type': 'application/json'});
// 	let query; 
// 	if (id) { 
// 		// if id is passed then return prodcuts belonging to that Category
// 		id = parseInt(id);
// 		query = "select id, name from product where catId = ?";
// 		db.all(query, [id], (err, rows) => {
// 			if (err == null) {
// 			res.write(JSON.stringify(rows));
// 			res.end();
// 			} else {
// 			res.end("Error " + err);
// 			}
// 		});
// 	}
//   });



/*if empty and qty is positive then add it to the session, 
else check if entry exist in session and update it accordingly: if qty is zero then entry should be removed from the list
*/
// client sends item as {"id":"S50_1514", "price":58.58, "qty":"1"}

//plan A: negative value decrement, zero delete, positive qty increment for existing entries.

// app.use("/cart", (req, res) => {
// 	res.header('Access-Control-Allow-Origin', '*');

// 	//checking if parameter exist as in /cart?item=JSON_Object
// 	//will fail when nothing is passed as in /cart 
// 	if(req.query.item)
// 	{
// 	//parsing the object passed
// 	let JSONitem = JSON.parse(req.query.item);

// 	//tring to add the name of the product to the list
// 	//console.log("type of JSONitem.id: " + typeof JSONitem.id);
// 	//var itemName = nameOfProduct(JSONitem.id);
// 	//JSONitem[name] = itemName;

// 	//checking the object have the required data
// 	if (JSONitem && JSONitem.id && JSONitem.price) {
// 		//a list of varaibles
// 		var targetID = JSONitem.id;
// 		var newQTY = JSONitem.qty;
// 		var incrementedQTY = 0;
// 		let incrementedJSON = JSONitem;
// 		var isItemFound = false;

// 		//if session doesn't exist then create one then if qty is positive add the object
// 		if (!req.session.cart)  //JAVA equlivant: req.getSession().getAttribute("cart")
// 		{
// 			req.session.cart = [];
// 			if (JSONitem.qty > 0) {
// 				req.session.cart.push(JSONitem);
// 			}
// 		}
// 		//if session exist but empty and request has positive qty: add it
// 		else if (req.session.cart && req.session.cart.length == 0 && JSONitem.qty > 0)
// 		{
// 			req.session.cart.push(JSONitem);
// 		}
// 		//session exist: if object already exist then delete it first then if new_qty + old_qty is positive add it.
// 		else {
// 			req.session.cart.forEach((object, index) => {
// 				//is this the object we want
// 				if (JSON.parse(JSON.stringify(object)).id == targetID) {
// 					isItemFound = true;
// 					//user wants to delete existing record
// 					if (newQTY == 0 || (Number(newQTY) + Number(JSON.parse(JSON.stringify(object)).qty) == 0))
// 					{
// 						req.session.cart.splice(index, 1);
// 					}
// 					//updating entry in the session
// 					else
// 					{
// 						incrementedQTY = Number(newQTY) + Number(JSON.parse(JSON.stringify(object)).qty);
// 						incrementedJSON.qty = incrementedQTY;
// 						req.session.cart.splice(index, 1);
// 						req.session.cart.splice(index, 0, incrementedJSON);

// 					}
// 				}
// 			});
// 			//if no recored is found and new_qty is positive then add it
// 			if(!isItemFound && newQTY > 0)
// 			{
// 				req.session.cart.push(JSONitem);
// 			}
// 		}
// 	}
// }

// 	//this might not be part of the requirnment: to return json instead of text
// 	res.writeHead(200, { 'Content-Type': 'application/json' });
// 	//in all cases this will send the cart
// 	res.end(JSON.stringify(req.session.cart));
// });

app.use("/productname", (req, res) => {
	res.header('Access-Control-Allow-Origin', '*');
	let id = req.query.id;
	let query = "select name from product where id = ?";
	db.all(query, [id], (err, name) => {
		if (err == null) {
			res.writeHead(200, {
				'Content-Type': 'text/plain'
			});
			res.write(name);
			res.end();
		} else {
			res.end("Error " + err);
		}
	});
});

app.use("/productinfo", (req, res) => {
	let id = req.query.id;
	let query = "select * from product where id = ?";
	db.all(query, [id], (err, rows) => {
		if (err == null) {
			res.writeHead(200, {
				'Content-Type': 'application/json'
			});
			res.write(JSON.stringify(rows));
			res.end();
		} else {
			res.end("Error " + err);
		}
	});
});

app.use('/product', function (req, res) {
	let id = req.query.id;
	res.header('Access-Control-Allow-Origin', '*');
	res.writeHead(200, {
		'Content-Type': 'application/json'
	});
	let query;
	// sql injection vulnerability:
	//let query = "select id, name from product where catid = " + id;
	//db.all(query, [], (err, rows) =>
	// prepared statement:
	if (id) {
		query = "select * from product where id = ?";
		db.all(query, [id], (err, rows) => {
			if (err == null) {
				res.write(JSON.stringify(rows));
				res.end();
			} else {
				res.end("Error " + err);
			}
		});
	}
});

app.use('/Catalog', function (req, res) {
	//reading the parameter if exist!
	res.header('Access-Control-Allow-Origin', '*');
	let id = req.query.id;
	// prepared statement:
	let query = "select id, name from Category where id = ?";
	//If the id parameter is missing then the return should be an array of json objects for all rows in the table.
	if (id === undefined) {
		query = "select id, name from Category";
	}
	//[id] is to replace the "?" used in the query
	db.all(query, [id], (err, rows) => {
		if (err == null) {
			//The return should be mimed as "application/json" in the response's content type.
			res.writeHead(200, {
				'Content-Type': 'application/json'
			});
			res.write(JSON.stringify(rows));
			res.end();
		} else {
			res.end("Error " + err);
		}
	});
});
// Another version of Catalog 
// app.use('/Catalog', function (req, res) {
// 	let id = req.query.id;
// 	res.writeHead(200, {
// 	  'Content-Type': 'application/json'
// 	});

// 	let query;

// 	// prepared statement:
// 	if (id != undefined) {

// 	  // if id exists
// 	  id = parseInt(id);
// 	  query = "select id, name from Category where id = ?";
// 	  db.all(query, [id], (err, rows) =>
// 		// searching for the id in the CATEGORY table

// 		{
// 		  if (err == null) {
// 			res.write(JSON.stringify(rows));
// 			res.end();
// 		  } else {
// 			res.end("Error " + err);
// 		  }
// 		});
// 	} else {

// 	  // if the isn't passed in the query string
// 	  query = "select id, name from Category";
// 	  db.all(query, (err, rows) =>
// 		// return id,name of all rows in the Category
// 		{
// 		  if (err == null) {
// 			res.end(JSON.stringify(rows));

// 		  } else {
// 			res.end("Error " + err);
// 		  }
// 		});
// 	}
//   });

app.use('/cartprice', function (req, res) {
	res.header('Access-Control-Allow-Origin', 'http://localhost:4200');
	res.header('Access-Control-Allow-Credentials', 'true');
	res.writeHead(200, {
		'Content-Type': 'application/json'
	});
	let totalPrice = 0.0;
	localCart.forEach((item) => {
		totalPrice += (parseFloat(item.price) * parseInt(item.qty))
	});
	res.end(totalPrice.toString());
});

app.use('/cart', function (req, res) {
	res.header('Access-Control-Allow-Origin', 'http://localhost:4200');
	res.header('Access-Control-Allow-Credentials', 'true');
	res.writeHead(200, {
		'Content-Type': 'application/json'
	});
	if (!req.session.cart) {
		console.log("Cart doesn't exists");
		// if cart dosen't exists
		req.session.cart = [];
		if (req.query.item) {
			// if item params exists
			let item = JSON.parse(req.query.item);
			if (item.qty > 0) {
				item.qty = 1;
				req.session.cart.push(item);
				localCart = req.session.cart;
				res.end(JSON.stringify(req.session.cart));
			} else if (item.qty <= 0) {
				// if qty is 0 or less
				localCart = req.session.cart;
				res.end(JSON.stringify(req.session.cart));
			}
		} else if (!req.query.item) {
			// if item param isn't passed in query string
			localCart = req.session.cart;
			res.end(JSON.stringify(req.session.cart));
		}
	} else if (req.session.cart) {
		// if the cart exists
		console.log("The cart exists");
		if (!req.query.item) {
			// if the item param isn't passed
			localCart = req.session.cart;
			res.end(JSON.stringify(req.session.cart));
		} else if (req.query.item) {
			// if the item param is passed
			let item = JSON.parse(req.query.item);
			let cart = req.session.cart;
			cart.forEach((e, i) => {
				if (e.id == item.id) {
					// if the item exists in cart
					if (item.qty > 0) {
						e.qty = e.qty + 1;
						localCart = req.session.cart;
						res.end(JSON.stringify(req.session.cart));
					} else if (item.qty <= 0) {
						// qty passed is 0 or less
						e.qty = e.qty - 1;
						if (e.qty <= 0) {
							cart.splice(i, 1);
							localCart = req.session.cart;
							res.end(JSON.stringify(req.session.cart));
						} else if (e.qty > 0) {
							localCart = req.session.cart;
							res.end(JSON.stringify(req.session.cart));
						}
					}
				}
			});
			// if the item doesn't exists previously
			if (item.qty > 0) {
				item.qty = 1;
				cart.push(item);
				localCart = req.session.cart;
				res.end(JSON.stringify(req.session.cart));
			} else if (item.qty <= 0) {
				localCart = req.session.cart;
				res.end(JSON.stringify(req.session.cart));
			}
		}
	}
});

app.use('/geonode', function (req, res) {
	let lat = req.query.lat;
	let lon = req.query.lon;
	if (!req.session.geonode) {
		// if no session
		res.writeHead(200, {'content-type': 'text/html'});
		req.session.geonode = [];
		req.session.geonode.push(lat);
		req.session.geonode.push(lon);
		console.log(req.session.geonode);
		res.end('Received!');
	} else {
		// if there is a session
		let contents = fs.readFileSync(geoFilePath, 'utf8');
		var address = contents.split('\n');
		let port = parseInt(address[1].trim());
		let array = req.session.geonode
		array.push(lat);
		array.push(lon);
		let coordinates = array[0] + " " + array[1] + " " + array[2] + " " + array[3];
		let client = net.createConnection({
			host: "127.0.0.1",
			port: port
		}, () => {
			console.log("Connected to TCP:");
			client.write(coordinates);
			client.end();
		});
		client.on('data', (data) => {
			console.log(array);
			array.splice(0, 2);
			console.log(array);
			res.end(data);
			client.destroy();
		});
		client.on('error', (err) => {
			res.write(err.toString());
			console.log("ERROR!");
			client.end();
		});
	}
});

app.use('/Trip', function (req, res) {
	let from = req.query.from;
	let to = req.query.to;
	// creating a URL
	let url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + from + "&destinations=" + to + "&departure_time=now&key=AIzaSyBxwV7M-rbBVqKBJBY5fTMYGtAow-Jrz0w";
	// making a get request to the URL
	https.get(url, (resp) => {
		let data = '';
		resp.on('data', (x) => {
			data += x;
		});

		// once the data is completed
		resp.on('end', () => {
			res.write("Optimal Distance: " + JSON.parse(data).rows[0].elements[0].distance.text);
			res.end("\nOptimal Time: " + JSON.parse(data).rows[0].elements[0].duration.text);
		});
	}).on("error", (err) => {
		res.send(err);
	});
});


// --------------------------------------SERVER
var server = app.listen(port, function () {
	var host = server.address().address;
	var port = server.address().port;
	console.log('Listening at http://%s:%d', host, port);
});