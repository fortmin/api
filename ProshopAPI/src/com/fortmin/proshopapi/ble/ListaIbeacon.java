package com.fortmin.proshopapi.ble;

import java.util.ArrayList;
import com.fortmin.proshopapi.ble.Ibeacon;

public class ListaIbeacon {
	/**objeto Singleton*/
	private static ListaIbeacon ListaSingleton= new ListaIbeacon();
	private ArrayList<Ibeacon> listaBeacons; 
	private int elementos=0;
	
private ListaIbeacon	(){
	listaBeacons=new ArrayList<Ibeacon>();
	
}
public static ListaIbeacon getListaBeacons() {
	 return ListaSingleton;
	}
public void add(Ibeacon ibeacon){
	if (!estaIbeacon(ibeacon)){
	 listaBeacons.add(elementos,ibeacon);
	 elementos++;
	}
	else
	 update(ibeacon);	
}
public void remove(Ibeacon ibeacon){
	Ibeacon aux_beacon;
	int indice=0;
	aux_beacon=listaBeacons.get(indice);
	boolean iguales=false;
	while(indice<=elementos && !iguales){
		iguales=IgualesBeacon(aux_beacon,ibeacon);
		indice++;
		if (!(indice==elementos))
		  aux_beacon=listaBeacons.get(indice);
	}
	if (iguales){
		indice--;
	 	listaBeacons.remove(indice);
	}
}
public void update(Ibeacon ibeacon){
	Ibeacon aux_beacon;
	int indice=0;
	aux_beacon=listaBeacons.get(indice);
	boolean iguales=false;
	while(indice<=elementos && !iguales){
		iguales=IgualesBeacon(aux_beacon,ibeacon);
		indice++;
		if (!(indice==elementos))
		 aux_beacon=listaBeacons.get(indice);
	}
	if (iguales){
		indice--;
	 	listaBeacons.set(indice, ibeacon);
	}
}
public boolean IgualesBeacon(Ibeacon b1, Ibeacon b2){
	String uuid1,uuid2;
	boolean iguales_uuid,iguales_major,iguales_minor;
	int major1,major2,minor1,minor2;
	uuid1=b1.getProximityUuid();
	uuid2=b2.getProximityUuid();
	major1=b1.getMajor();
	major2=b2.getMajor();
	minor1=b1.getMinor();
	minor2=b2.getMinor();
	iguales_uuid=uuid1.equals(uuid2);
	iguales_major=(major1==major2);
	iguales_minor=(minor1==minor2);
	return iguales_uuid && iguales_major && iguales_minor;
	
	
}

public boolean estaIbeacon(Ibeacon ibeacon){
	Ibeacon aux_beacon;
	int indice=0;
	aux_beacon=listaBeacons.get(indice);
	boolean iguales=false;
	while(indice<=elementos && !iguales){
		iguales=IgualesBeacon(aux_beacon,ibeacon);
		indice++;
		if (!(indice==elementos))
		 aux_beacon=listaBeacons.get(indice);
	}
	return iguales;
}

public ArrayList<Ibeacon> IbeaconsEncendidos(){
	int indice=0;
	ArrayList<Ibeacon> beacons= new ArrayList<Ibeacon>();
	while (indice<=elementos){
		if(listaBeacons.get(indice).clienteCerca())
			beacons.add(listaBeacons.get(indice));
		indice++;    
	}
	return beacons;
	}

public void calibrarBeacon(Ibeacon ibeacon){
	ibeacon.setCalibracion(ibeacon.getRssi());
	update(ibeacon);
}
	
}
