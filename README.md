The follwing code initializes the barcode reader
Call one time at application startup

	navigator.aidc.init(function () {
		console.log("Reader initialized");
		navigator.aidc.claim(function () {
			console.log("Reader claimed");
		}, function (err) { console.log(err); }); 
	}, function (err) { console.log(err); });

	
To register a callback for barcode reads
	navigator.aidc.register(function (event) {
		if(event.success) {
			console.log(event.data);
		} else {
			// scan button pressed, no barcode read
		}
	});
	
	This callback is called both when a barcode is read, and when it's not read.
	Check the event.success field.
	
	Each call to the navigator.aidc.register will replace the current callback
	
To unregister the current callback
	navigator.aidc.unregister();
	

To enable the trigger button
	navigator.aidc.enableTrigger();
	
	The trigger button is enable by default

To disable the trigger button
	navigator.aidc.disableTrigger();
	

In the apps pause event call this
	navigator.aidc.release(function () { console.log("Reader released"); }, function (err) { console.log(err); });
	
In the apps resume event call this
	navigator.aidc.claim(function () { console.log("Reader claimed"); }, function (err) { console.log(err); });
