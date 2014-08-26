package com.fortmin.proshopapi.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class ProShopBleMgr {

	/*
	 * Averiguo si el celular es capaz de comunicarse con dispositivos Bluetooth
	 * Low Energy BLE esta disponible a partir del API Level 18 (JELLY_BEAN_MR2)
	 */
	public boolean bleSoportado(Activity activity) {
		boolean soporta = false;
		final BluetoothManager manager = this.getBluetoothManager(activity);
		if (manager != null) { // Chequear si BT esta disponible
			final BluetoothAdapter adapter = this.getBluetoothAdapter(manager);
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
	 * Devuelve el BluetoothManager del dispositivo
	 */
	public BluetoothManager getBluetoothManager(Activity activity) {
		return (BluetoothManager) activity
				.getSystemService(Context.BLUETOOTH_SERVICE);
	}

	/*
	 * Devuelve el BluetoothAdapter correspondiente al BluetoothManager
	 */
	public BluetoothAdapter getBluetoothAdapter(BluetoothManager manager) {
		return (BluetoothAdapter) manager.getAdapter();
	}

	/*
	 * Averiguo si el celular es capaz de comunicarse por Bluetooth
	 */
	public boolean bluetoothSoportado(Context context) {
		PackageManager pckMgr = context.getPackageManager();
		return pckMgr.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
	}

	/*
	 * Verificar si Bluetooth está habilitado
	 */
	public boolean bluetoothHabilitado(Activity activity) {
		boolean habilitado = false;
		if (bluetoothSoportado(activity.getApplicationContext())) {
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
	 * Inicializar la clase EscucharIbeacons
	 */
	public EscucharIbeacons inicializarBLE(Activity activity) {
		return new EscucharIbeacons(activity);
	}

}
