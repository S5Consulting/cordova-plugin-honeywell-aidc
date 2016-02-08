window.addEventListener('barcode', function (event) {
	console.log(event.barcode);
}, false);

navigator.aidc.init(function () {
	console.log("Reader initialized");
	navigator.aidc.claim(function () {
		console.log("Reader claimed");
	}, function (err) { console.log(err); }); 
}, function (err) { console.log(err); });
