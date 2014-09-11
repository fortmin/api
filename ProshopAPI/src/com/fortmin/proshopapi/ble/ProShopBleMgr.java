package com.fortmin.proshopapi.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.os.Build;

public class ProShopBleMgr {

	private BluetoothManager manager;
	private BluetoothAdapter adapter;
	private boolean bluetoothSoportado;
	private boolean bluetoothHabilitado;

	public ProShopBleMgr(boolean bluetoothSoportado,
			boolean bluetoothHabilitado, BluetoothManager manager,
			BluetoothAdapter adapter) {
		this.manager = manager;
		this.adapter = adapter;
		this.bluetoothSoportado = bluetoothSoportado;
		this.bluetoothHabilitado = bluetoothHabilitado;
	}

	/*
	 * Averiguo si el celular es capaz de comunicarse con dispositivos Bluetooth
	 * Low Energy BLE esta disponible a partir del API Level 18 (JELLY_BEAN_MR2)
	 */
	public boolean bleSoportado(Activity activity) {
		boolean soporta = false;
		if (manager != null) { // Chequear si BT esta disponible
			if (adapter != null) {
				PackageManager pckMgr = activity.getApplicationContext()
						.getPackageManager();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
					soporta = pckMgr
							.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
				}
			}
		}
		return soporta;
	}

	/*
	 * Inicializar la clase EscucharIbeacons
	 */
	public EscucharIbeacons inicializarBLE(Activity activity) {
		return new EscucharIbeacons(activity);
	}

}
