package com.fortmin.proshopapi.ble;

import java.util.UUID;

public class Ibeacon {

	private String valor_nombre;
	private int rssi;
	private byte[] scanRecord;
	private int major;
	private int minor;
	double txPower;
	private String proximityUuid;
	private boolean calibrado;
	private int distancia;
	// Posicion de comienzo dentro del scanRecord para obtener
	// los valores de major, minor y txPower
	private final int startByte = 2;

	public Ibeacon(String nombre, int v_rssi) {
		valor_nombre = nombre;
		this.rssi = v_rssi;
		this.calibrado = false;
	}

	public String getValor_nombre() {
		return valor_nombre;
	}

	public void setValor_nombre(String valor_nombre) {
		this.valor_nombre = valor_nombre;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public byte[] getScanRecord() {
		return scanRecord;
	}

	public void setScanRecord(byte[] scanRecord) {
		this.scanRecord = scanRecord;
		major = (scanRecord[startByte + 23] & 0xff) * 0x100
				+ (scanRecord[startByte + 24] & 0xff);
		minor = (scanRecord[startByte + 25] & 0xff) * 0x100
				+ (scanRecord[startByte + 26] & 0xff);
		txPower = (scanRecord[startByte + 27] & 0xff) * 0x100
				+ (scanRecord[startByte + 28] & 0xff);
		; // this one is signed
		byte[] uuid_ent = new byte[16];
		System.arraycopy(scanRecord, 9, uuid_ent, 0, 16);
		/*
		 * uuid_ent[0]=scanRecord[9]; uuid_ent[1]=scanRecord[10];
		 * uuid_ent[2]=scanRecord[11]; uuid_ent[3]=scanRecord[12];
		 * uuid_ent[4]=scanRecord[13]; uuid_ent[5]=scanRecord[14];
		 * uuid_ent[6]=scanRecord[15]; uuid_ent[7]=scanRecord[16];
		 * uuid_ent[8]=scanRecord[17]; uuid_ent[9]=scanRecord[18];
		 * uuid_ent[10]=scanRecord[19]; uuid_ent[11]=scanRecord[20];
		 * uuid_ent[12]=scanRecord[21]; uuid_ent[13]=scanRecord[22];
		 * uuid_ent[14]=scanRecord[23]; uuid_ent[15]=scanRecord[24];
		 */
		UUID uuid = UUID.nameUUIDFromBytes(uuid_ent);
		this.proximityUuid = uuid.toString();
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public double getTxPower() {
		return txPower;
	}

	public void setTxPower(int txPower) {
		this.txPower = txPower;
	}

	public String getProximityUuid() {
		return proximityUuid;
	}

	public void setProximityUuid(String proximityUuid) {
		this.proximityUuid = proximityUuid;
	}

	public boolean estaCalibrado() {
		return this.calibrado;
	}

	public float getDistancia() {
		return distancia;
	}

	public String getValor_rssi() {
		return String.valueOf(rssi);
	}

	public int darValorRssi() {
		return rssi;
	}

	public void setValorRssi(int valor) {
		rssi = valor;
	}

	public void setCalibracion(int rssi_ibeacon) {
		// potencia es el valor de rssi captado por el movil a la distancia de 1
		// metro
		// rssi_ibeacon es el rssi que tiene el dispositivo que transmite
		// tomo como referencia el rss a la distancia requerida
		this.distancia = Math.abs(rssi_ibeacon);
		this.calibrado = true;
		// this.ni=(float) ((-rssi_ibeacon-rssi_metro)/(10*Math.log10(A)));
	}

	public boolean clienteCerca() {
		double diferencia = Math.abs(rssi) - distancia;
		if (diferencia > 2)
			return false;
		else
			return true;
	}

	public double calculateAccuracy() {
		double txPower = -95;
		double ratio = rssi * 1.0 / txPower;
		if (ratio < 1.0) {
			return Math.pow(ratio, 10);
		} else {
			double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
			return accuracy;
		}
	}
}
