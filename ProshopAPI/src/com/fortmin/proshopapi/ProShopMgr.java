package com.fortmin.proshopapi;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.util.Log;

import com.fortmin.proshopapi.ble.ProShopBleMgr;
import com.fortmin.proshopapi.nfc.ProShopNFCMgr;

/*
 * Clase principal a la cual se invoca para solicitar el Manager de la Tecnologia
 * correspondiente.
 */
public class ProShopMgr {

	private Context context = null;
	private final String TAG = "PSHAPI";

	public ProShopMgr(Context context) {
		this.context = context;
	}

	public ProShopMgr() {

	}

	private void log(String logtxt) {
		String mens = this.getClass().getName() + "->";
		if (logtxt != null)
			mens = mens.concat("->" + logtxt);
		Log.i(TAG, mens);
	}

	/*
	 * Averiguo si el celular tiene soporte NFC
	 */
	public boolean soportaNFC() {
		log("soportaNFC");
		PackageManager pckMgr = context.getPackageManager();
		return pckMgr.hasSystemFeature(PackageManager.FEATURE_NFC);
	}

	/*
	 * Chequear si el NFC se encuentra habilitado en este momento
	 */
	public boolean nfcHabilitado() {
		log("nfcHabilitado");
		boolean habilitado = false;
		if (soportaNFC()) {
			NfcManager nfcMgr = (NfcManager) context
					.getSystemService(Context.NFC_SERVICE);
			if (nfcMgr != null) {
				NfcAdapter nfcAdapter = nfcMgr.getDefaultAdapter();
				if (nfcAdapter != null) {
					habilitado = nfcAdapter.isEnabled();
				}
			}
		}
		return habilitado;
	}

	/*
	 * Devuelve el entorno NFC
	 */
	public ProShopNFCMgr getNFC() {
		log("getNFC");
		ProShopNFCMgr psNfc = new ProShopNFCMgr(soportaNFC(), nfcHabilitado(),
				context);
		return psNfc;
	}

	/*
	 * Devuelve el entorno BLE
	 */
	public ProShopBleMgr getBLE(Activity activity) {
		log("getBLE");
		ProShopBleMgr psBle = new ProShopBleMgr(bluetoothSoportado(),
				bluetoothHabilitado(activity), getBluetoothManager(activity),
				getBluetoothAdapter(getBluetoothManager(activity)));
		return psBle;
	}

	/*
	 * Devuelve el BluetoothManager del dispositivo
	 */
	public BluetoothManager getBluetoothManager(Activity activity) {
		log("getBluetoothManager");
		return (BluetoothManager) activity
				.getSystemService(Context.BLUETOOTH_SERVICE);
	}

	/*
	 * Devuelve el BluetoothAdapter correspondiente al BluetoothManager
	 */
	public BluetoothAdapter getBluetoothAdapter(BluetoothManager manager) {
		log("getBluetoothAdapter");
		return (BluetoothAdapter) manager.getAdapter();
	}

	/*
	 * Averiguo si el celular es capaz de comunicarse por Bluetooth
	 */
	public boolean bluetoothSoportado() {
		log("bluetoothSoportado");
		PackageManager pckMgr = context.getPackageManager();
		return pckMgr.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
	}

	/*
	 * Chequear si el Bluetooth se encuentra habilitado en este momento
	 */
	public boolean bluetoothHabilitado(Activity activity) {
		log("bluetoothHabilitado");
		boolean habilitado = false;
		if (bluetoothSoportado()) {
			BluetoothManager btMgr = this.getBluetoothManager(activity);
			if (btMgr != null) {
				BluetoothAdapter btAdapter = this.getBluetoothAdapter(btMgr);
				if (btAdapter != null) {
					habilitado = btAdapter.isEnabled();
				}
			}
		}
		return habilitado;
	}

	/*
	 * Tabla de referencia entre API Level y Version Codes: 9 GINGERBREAD
	 * Android 2.3 Gingerbread 10 GINGERBREAD_MR1 Android 2.3.3 Gingerbread 11
	 * HONEYCOMB Android 3.0 Honeycomb 12 HONEYCOMB_MR1 Android 3.1 Honeycomb 13
	 * HONEYCOMB_MR2 Android 3.2 Honeycomb 14 ICE_CREAM_SANDWICH Android 4.0 Ice
	 * Cream Sandwich 15 ICE_CREAM_SANDWICH_MR1 Android 4.0.3 Ice Cream Sandwich
	 * 16 JELLY_BEAN Android 4.1 Jellybean 17 JELLY_BEAN_MR1 Android 4.2
	 * Jellybean 18 JELLY_BEAN_MR2 Android 4.3 Jellybean 19 KITKAT Android 4.4
	 * KitKat
	 */

}
