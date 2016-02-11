package com.lexitgroup.honeywell;

import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.BarcodeReader.BarcodeListener;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.UnsupportedPropertyException;

import android.util.Log;
import android.widget.Toast;

public class Aidc extends CordovaPlugin implements BarcodeListener {

	private static String TAG = Aidc.class.getName();

	private BarcodeReader barcodeReader;
	private AidcManager manager;

	private CallbackContext currentCallbackContext = null;

	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		if ("init".equals(action)) {
			AidcManager.create(this.cordova.getActivity(), new CreatedCallback() {

				@Override
				public void onCreated(AidcManager aidcManager) {
					manager = aidcManager;
					barcodeReader = manager.createBarcodeReader();

					if (barcodeReader != null) {

						barcodeReader.addBarcodeListener(Aidc.this);

						// Map<String, Object> asdfsadf =
						// barcodeReader.getAllDefaultProperties();
						// for (String key : asdfsadf.keySet()) {
						// Log.d("chromium", key + "=" + asdfsadf.get(key));
						// }

						// set the trigger mode to auto control
						try {
							barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);
						} catch (UnsupportedPropertyException e) {
							Toast.makeText(Aidc.this.cordova.getActivity(), "Failed to apply properties", Toast.LENGTH_SHORT).show();
						}

						Map<String, Object> properties = new HashMap<String, Object>();
						// Set Symbologies On/Off
						properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
						properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
						properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
						properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
						properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
						properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
						properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, true);
						properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
						properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
						properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
						properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false);
						// Set Max Code 39 barcode length
						properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 48);
						// Turn on center decoding
						properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
						// Disable bad read response, handle in onFailureEvent
						properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, false);

						properties.put(BarcodeReader.PROPERTY_DATA_PROCESSOR_LAUNCH_BROWSER, false);

						// Apply the settings
						barcodeReader.setProperties(properties);

						callbackContext.success();
					} else {
						callbackContext.error("Failed to open barcode reader");
					}
				}
			});

			return true;
		} else if ("claim".equals(action)) {
			if (barcodeReader != null) {
				try {
					barcodeReader.claim();

					callbackContext.success();
				} catch (ScannerUnavailableException e) {
					e.printStackTrace();
					callbackContext.error("Unable to claim reader");
				}
			} else {
				callbackContext.error("Reader not open");
			}

			return true;
		} else if ("release".equals(action)) {
			if (barcodeReader != null) {
				barcodeReader.release();
				callbackContext.success();
			} else {
				callbackContext.error("Reader not open");
			}

			return true;
		} else if ("register".equals(action)) {
			currentCallbackContext = callbackContext;

			PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
			result.setKeepCallback(true);
			callbackContext.sendPluginResult(result);

			return true;
		} else if ("unregister".equals(action)) {
			currentCallbackContext = null;

			return true;
		} else if ("enableTrigger".equals(action)) {
			Log.d(TAG, "enableTrigger");

			try {
				barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);

				callbackContext.success();
			} catch (UnsupportedPropertyException e) {
				callbackContext.error(e.getMessage());
			}

			return true;
		} else if ("disableTrigger".equals(action)) {
			Log.d(TAG, "disableTrigger");

			try {
				barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_DISABLE);

				callbackContext.success();
			} catch (UnsupportedPropertyException e) {
				callbackContext.error(e.getMessage());
			}

			callbackContext.success();

			return true;
		}

		return false;
	}

	@Override
	public void onDestroy() {
		if (barcodeReader != null) {
			// close BarcodeReader to clean up resources.
			barcodeReader.close();
			barcodeReader = null;
		}

		if (manager != null) {
			// close AidcManager to disconnect from the scanner service.
			// once closed, the object can no longer be used.
			manager.close();
		}

		super.onDestroy();
	}

	@Override
	public void onBarcodeEvent(BarcodeReadEvent arg0) {
		Log.d(TAG, arg0.getBarcodeData());

		if (currentCallbackContext != null) {
			try {
				JSONObject obj = new JSONObject();
				obj.put("success", true);
				obj.put("data", arg0.getBarcodeData());

				PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
				result.setKeepCallback(true);
				currentCallbackContext.sendPluginResult(result);
			} catch (Exception x) {
				Log.e(TAG, x.getMessage());
			}
		}
	}

	@Override
	public void onFailureEvent(BarcodeFailureEvent arg0) {
		// Log.d(TAG, "No data");

		if (currentCallbackContext != null) {
			try {
				JSONObject obj = new JSONObject();
				obj.put("success", false);

				PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
				result.setKeepCallback(true);
				currentCallbackContext.sendPluginResult(result);
			} catch (Exception x) {
				Log.e(TAG, x.getMessage());
			}
		}
	}
}