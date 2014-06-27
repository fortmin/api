package com.fortmin.proshopapi.ble;

import android.bluetooth.BluetoothDevice;

public class DispositivoBT {
	
	String nombre;				// nombre amigable del dispositivo
	String descripcion;			// descripcion del dispositivo en formato String
	String direccion;			// direccion del dispositivo en formato String
	
	
	public DispositivoBT(BluetoothDevice btd) {
		descripcion = btd.getBluetoothClass().toString();
		nombre = btd.getName();
		direccion = btd.getAddress();
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
}
