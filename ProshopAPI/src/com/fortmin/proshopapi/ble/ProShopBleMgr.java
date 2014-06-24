package com.fortmin.proshopapi.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class ProShopBleMgr {
	
	/*
	 * Averiguo si el celular es capaz de comunicarse con dispositivos Bluetooth Low Energy
	 * BLE esta disponible a partir del API Level 18 (JELLY_BEAN_MR2)
	 */	
	public boolean soportaBLE(Context context) {
		boolean soporta = false;
		PackageManager pckMgr = context.getPackageManager();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			soporta = pckMgr.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
		}
		return soporta;
	}

	/*
	 * Averiguo si el celular es capaz de comunicarse por Bluetooth
	 */
	public boolean soportaBluetooth(Context context) {
		PackageManager pckMgr = context.getPackageManager();
		return pckMgr.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
	}
	
	/*
	 * Verificar si Bluetooth está habilitado
	 */
	public boolean bluetoothHabilitado(Context context) {
		boolean habilitado = false;
		if (soportaBluetooth(context)) {
			BluetoothManager btMgr = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
			if (btMgr != null) {
				BluetoothAdapter btAdapter = btMgr.getAdapter();
				if (btAdapter != null) {
					habilitado = (btAdapter.getState() == BluetoothAdapter.STATE_ON);
				}
			}
		}
		return habilitado;
	}

}
