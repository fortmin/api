package com.fortmin.proshopapi.ble;

import java.util.ArrayList;

import com.fortmin.proshopapi.ListaIbeacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

public class EscuchoIbeacon {
	private Activity mParent = null;    
	private boolean mConnected = false;
	private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice  mBluetoothDevice = null;
    private final Handler mTimerHandler = new Handler();
    private boolean mTimerEnabled = false;
    private ListaIbeacon ibeacons;
    
	/* updated de RSSI en milisegundos*/
    private static final int RSSI_UPDATE_TIME_INTERVAL = 15000; // 15 seconds

    /* callback object through which we are returning results to the caller */
    private BleWrapperUiCallbacks mUiCallback = null;
    /* define NULL object for UI callbacks */
    private static final BleWrapperUiCallbacks NULL_CALLBACK = new BleWrapperUiCallbacks.Null(); 
    
   
    /* creates BleWrapper object, set its parent activity and callback object */
    public EscuchoIbeacon(Activity parent, BleWrapperUiCallbacks callback) {
    	this.mParent =parent;
    	mUiCallback = callback;
    	ibeacons=ListaIbeacon.getListaBeacons();
    	if(mUiCallback == null) {
			mUiCallback = NULL_CALLBACK;
		}
    }
    public EscuchoIbeacon(Activity parent){
    	this.mParent =parent;
    	//mUiCallback=new BleWrapperUiCallbacks.Null();
    	
    }
    
    public BluetoothManager           getManager() { return mBluetoothManager; }
    public BluetoothAdapter           getAdapter() { return mBluetoothAdapter; }
    public BluetoothDevice            getDevice()  { return mBluetoothDevice; }
    public boolean                    isConnected() { return mConnected; }

	/* run test and check if this device has BT and BLE hardware available */
	public boolean checkBleHardwareAvailable() {
		// First check general Bluetooth Hardware:
		// get BluetoothManager...
		final BluetoothManager manager = (BluetoothManager) mParent.getSystemService(Context.BLUETOOTH_SERVICE);
		if(manager == null) {
			return false;
		}
		// .. and then get adapter from manager
		final BluetoothAdapter adapter = manager.getAdapter();
		if(adapter == null) {
			return false;
		}
		
		// and then check if BT LE is also available
		boolean hasBle = mParent.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
		return hasBle;
	}    

	
	/* before any action check if BT is turned ON and enabled for us 
	 * call this in onResume to be always sure that BT is ON when Your
	 * application is put into the foreground */
	public boolean isBtEnabled() {
		final BluetoothManager manager = (BluetoothManager) mParent.getSystemService(Context.BLUETOOTH_SERVICE);
		if(manager == null) {
			return false;
		}
		
		final BluetoothAdapter adapter = manager.getAdapter();
		if(adapter == null) {
			return false;
		}
		
		return adapter.isEnabled();
	}
	
	/* start scanning for BT LE devices around */
	public void startScanning() {
        mBluetoothAdapter.startLeScan(mDeviceFoundCallback);
	}
	
	
	/* stops current scanning */
	public void stopScanning() {
		mBluetoothAdapter.stopLeScan(mDeviceFoundCallback);	
	}
	
    /* initialize BLE and get BT Manager & Adapter */
    public boolean initialize() {
    	
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mParent.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }

        if(mBluetoothAdapter == null) {
			mBluetoothAdapter = mBluetoothManager.getAdapter();
		}
        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;    	
    }

  
    /* request new RSSi value for the connection*/
    public void readPeriodicalyRssiValue(final boolean repeat) {
    	mTimerEnabled = repeat;
    	// check if we should stop checking RSSI value
    	if(mConnected == false || mTimerEnabled == false) {
    		mTimerEnabled = false;
    		return;
    	}
    	
    	mTimerHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if( mBluetoothAdapter == null || mConnected == false)
				{
					mTimerEnabled = false;
					return;
				}
												
				// add call it once more in the future
				readPeriodicalyRssiValue(mTimerEnabled);
			}
    	}, RSSI_UPDATE_TIME_INTERVAL);
    }    
    
    /* starts monitoring RSSI value */
    public void startMonitoringRssiValue() {
    	readPeriodicalyRssiValue(true);
    }
    
    /* stops monitoring of RSSI value */
    public void stopMonitoringRssiValue() {
    	readPeriodicalyRssiValue(false);
    }
  
 
    private final BluetoothAdapter.LeScanCallback mDeviceFoundCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        	// en cada lectura guardo el rssi y todos los parametros del ibeacon
        	Ibeacon ibeacon= new Ibeacon("",0);
        	ibeacon.setValor_nombre(device.getName());
        	ibeacon.setValorRssi(rssi);
        	ibeacon.setScanRecord(scanRecord);
        	ibeacons.add(ibeacon);
        	
    		
        }
    };
    
    
   
    
}
