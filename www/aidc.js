var cordova = require('cordova');
var exec = require('cordova/exec');

var Aidc = function() {
	
	this.init = function (success_cb, error_cb) {
		exec(success_cb, error_cb, "HoneywellAidc", "init", []);
		exec(this._success, this._error, "HoneywellAidc", "callback", []);
	};
	
	this.claim = function (success_cb, error_cb) {
		exec(success_cb, error_cb, "HoneywellAidc", "claim", []);
	};
	
	this.release = function (success_cb, error_cb) {
		exec(success_cb, error_cb, "HoneywellAidc", "release", []);
	};
};

Aidc.prototype._success = function (code) {
	console.log('Aidc._success');
	
	cordova.fireWindowEvent('barcode', { code: code });
};

Aidc.prototype._error = function () {
	console.log('Aidc._error');
};

console.log("Loaded Honeywell Aidc JavaScript");

var aidc = new Aidc();
module.exports = aidc;
