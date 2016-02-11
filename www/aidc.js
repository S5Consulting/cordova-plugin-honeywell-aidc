var cordova = require('cordova');
var exec = require('cordova/exec');

var Aidc = function() {
	
	this.init = function (success_cb, error_cb) {
		exec(success_cb, error_cb, "HoneywellAidc", "init", []);
		//exec(this._success, this._error, "HoneywellAidc", "callback", []);
	};
	
	this.claim = function (success_cb, error_cb) {
		exec(success_cb, error_cb, "HoneywellAidc", "claim", []);
	};
	
	this.release = function (success_cb, error_cb) {
		exec(success_cb, error_cb, "HoneywellAidc", "release", []);
	};
	
	this.register = function(callback) {
		exec(callback, null, "HoneywellAidc", "register", []);
	};
	
	this.unregister = function () {
		exec(null, null, "HoneywellAidc", "unregister", []);
	};
	
	this.enableTrigger = function (success_cb, error_cb) {
		exec(success_cb, error_cb, "HoneywellAidc", "enableTrigger", []);
	};
	
	this.disableTrigger = function (success_cb, error_cb) {
		exec(success_cb, error_cb, "HoneywellAidc", "disableTrigger", []);
	};
};

Aidc.prototype._success = function (barcode) {
	console.log('Aidc._success');
	
	cordova.fireWindowEvent('barcode', { barcode: barcode });
};

Aidc.prototype._error = function () {
	console.log('Aidc._error');
};

console.log("Loaded Honeywell Aidc JavaScript");

var aidc = new Aidc();
module.exports = aidc;