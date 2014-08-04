package com.fortmin.proshopapi.nfc;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import com.fortmin.proshopapi.ProShopMgr;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

public class ProShopNFCMgr {
	
	private final String TAG = "PSHAPI";	
	private String tag_recibido;
	
    private void log(String logtxt) {
		String mens = this.getClass().getName()+"->";
		if (logtxt != null) mens = mens.concat("->"+logtxt);
		Log.i(TAG,mens);
	}

	/*
	 * Averiguo si el celular tiene soporte NFC
	 */
	public boolean soportaNFC(Context context) {
		log("soportaNFC");
		PackageManager pckMgr = context.getPackageManager();
		return pckMgr.hasSystemFeature(PackageManager.FEATURE_NFC);
	}

	/*
	 * Averiguo si el celular tiene soporte NFC Host Card Emulation
	 */
	public boolean soportaNFCHce(Context context) {
		log("soportaNFCHce");		
		boolean soporta = false;
		PackageManager pckMgr = context.getPackageManager();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {	
			soporta = pckMgr.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
		}
		return soporta;
	}
	
	/*
	 * Chequear si el NFC se encuentra habilitado en este momento
	 */
	public boolean nfcHabilitado(Context context) {
		log("nfcHabilitado");		
		boolean habilitado = false;
		if (soportaNFC(context)) {
			NfcManager nfcMgr = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
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
	 * Habilita la escucha del Tag para escritura o grabacion del mismo
	 */
	public boolean escucharTagNdefEscribir(Activity activity, Context context, Object clase) {
		log("escucharTagNdefEscribir");
		boolean result = false;
		if (nfcHabilitado(context)) {
	    	NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(context); 
	    	if (mNfcAdapter != null) {
	    		log("enableForegroundDispatch");
		    	PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, (Class<?>) clase).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		   	 	IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		   	 	IntentFilter[] mFilters = new IntentFilter[] { ndef, };
		   	 	String[][] mTechLists = new String[][] { new String[] { Ndef.class.getName() }, new String[] { NdefFormatable.class.getName() }};
		   	 	mNfcAdapter.enableForegroundDispatch(activity, mPendingIntent, mFilters, mTechLists);
		   	 	result = true;
	    	}
		}
		return result;
	}
	
	/*
	 * Deshabilita la escucha del Tag para escritura o grabacion del mismo
	 */
	public void noEscucharTagNdefGrabar(Activity activity, Context context) {
		log("noEscucharTagNdefGrabar");
		if (nfcHabilitado(context)) {
	    	NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(context); 
	    	if (mNfcAdapter != null) {
	    		log("enableForegroundDispatch");	    		
	            mNfcAdapter.disableForegroundDispatch(activity);
	    	}
		}
	}
	
	/*
	 * Escribe el mensaje NDEF en el Tag detectado
	 */
    public String escribirNdefMessageToTag(NdefMessage message, Tag detectedTag) {
    	log("escribirNdefMessageToTag");
    	String respuesta = "OK";
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(detectedTag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                	log("TAG_READ_ONLY");
                	respuesta = "TAG_READ_ONLY";
                }
                else {
                    if (ndef.getMaxSize() < size) {
                    	log("TAG_LLENO");
                    	respuesta = "TAG_LLENO";
                    }
                    else {
                    	log("writeNdefMessage");
                        ndef.writeNdefMessage(message);
                        ndef.close();                
                    }
                }
            } else {
                NdefFormatable ndefFormat = NdefFormatable.get(detectedTag);
                if (ndefFormat != null) {
                    try {
                    	log("format message");
                    	ndefFormat.connect();
                    	ndefFormat.format(message);
                    	ndefFormat.close();
                    } catch (IOException e) {
                    	log("TAG_FORMATO_INVALIDO");
                    	respuesta = "TAG_FORMATO_INVALIDO";
                    }
                } else {
                	log("TAG_NDEF_NO_SOPORTADO");
                    respuesta = "TAG_NDEF_NO_SOPORTADO";
                }
            }
        } catch (Exception e) {
        	log("TAG_FALLO_ESCRITURA");
        	respuesta = "TAG_FALLO_ESCRITURA";
        }
        return respuesta;
    }

	/*
	 * Obtiene el Tag descubierto a partir del Intent
	 */
	public Tag obtenerTagDescubierto(Intent intent) {
		Tag tag = null;
		if (intent != null) {
			log("obtener tag");
			tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);			
		}
		return tag;
	}
	
	/*
	 * Preparar mensaje NDEF para URL
	 */
	public NdefMessage prepararMensNdefUrl(String textoUrl) throws URISyntaxException {
		log("");
		NdefMessage nMessage = null;
		String url = (new URI(textoUrl)).normalize().toString();
		byte[] uriField = url.getBytes(Charset.forName("US-ASCII"));
		byte[] payload = new byte[uriField.length+1];
		payload[0] = UriNdefPrefixes.HTTP;
		System.arraycopy(uriField, 0, payload, 1, uriField.length);
		NdefRecord extRecord1 = NdefRecord.createUri(url);
		nMessage = new NdefMessage(new NdefRecord[] { extRecord1 });
		return nMessage;
	}
	
	/* 
	 * Preparar mensaje NDEF para email (mailto:)
	 */
	public NdefMessage prepararMensNdefMailto(String mail, String subject, String body) {
		log("");
		NdefMessage nMessage = null;
		String url = mail;
		if (subject != null) mail = mail.concat("?subject="+subject);
		if (body != null) mail = mail.concat("?body="+body);
		byte[] uriField = url.getBytes(Charset.forName("US-ASCII"));
		byte[] payload = new byte[uriField.length+1];
		payload[0] = UriNdefPrefixes.MAILTO;
		System.arraycopy(uriField, 0, payload, 1, uriField.length);
        NdefRecord URIRecord  = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
        nMessage= new NdefMessage(new NdefRecord[] { URIRecord });
		return nMessage;
	}

	/* 
	 * Preparar mensaje NDEF para telefono (tel:)
	 */
	public NdefMessage prepararMensNdefTel(String numtel) {
		log("");
		NdefMessage nMessage = null;
		String url = numtel;
		byte[] uriField = url.getBytes(Charset.forName("US-ASCII"));
		byte[] payload = new byte[uriField.length+1];
		payload[0] = UriNdefPrefixes.TEL;
		System.arraycopy(uriField, 0, payload, 1, uriField.length);
        NdefRecord URIRecord  = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
        nMessage= new NdefMessage(new NdefRecord[] { URIRecord });
		return nMessage;
	}
	/* 
	 * Preparar mensaje NDEF para grabar (tipo propietario)	 */
	  public NdefMessage prepararMensNdefPropietario(String identificador) {
		log("");
		NdefMessage nMessage = null;
		String externalType = "com.fortmin.proshopping:Shopping";
		String id = identificador;
		NdefRecord extRecord1 = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, externalType.getBytes(), new byte[0], id.getBytes());
	    nMessage = new NdefMessage(new NdefRecord[] { extRecord1});
       
		return nMessage;
	}
	
	/* 
	 * Preparar mensaje NDEF para SMS (sms:)
	 */
	public NdefMessage prepararMensNdefSMS(String numtel, String body) {
		log("");
		NdefMessage nMessage = null;
		String url = "sms:"+numtel;
		if (body != null) url = url.concat("?body="+body);
		byte[] uriField = url.getBytes(Charset.forName("US-ASCII"));
		byte[] payload = new byte[uriField.length+1];
		payload[0] = UriNdefPrefixes.HTTP;
		System.arraycopy(uriField, 0, payload, 1, uriField.length);
		NdefRecord extRecord1 = NdefRecord.createUri(url);
		nMessage = new NdefMessage(new NdefRecord[] { extRecord1 });
		return nMessage;
	}
	
	/*
	 * Preparar mensaje NDEF para tipo EXTERNAL_TYPE
	 * Ejemplo de tipo: "nfclab.com:smsService"
	 */
	public NdefMessage prepararMensNdefExternalType(String tipo, String datos) {
		log("");
		NdefMessage nMessage = null;
		NdefRecord registro = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, tipo.getBytes(), new byte[0], datos.getBytes());
		nMessage = new NdefMessage(new NdefRecord[] { registro });
		return nMessage;
	}
	private String escucharTag(Intent intent){
		   String tag_leido=null;
			NdefMessage[] messages = getNdefMessages(intent);
			for (int i = 0; i < messages.length; i++) {
				for (int j = 0; j < messages[0].getRecords().length; j++) {
					NdefRecord record = messages[i].getRecords()[j];
					String payload = new String(record.getPayload(), 0,
							record.getPayload().length,
							Charset.forName("UTF-8"));
					String delimiter = ":";
					String[] temp = payload.split(delimiter);
					tag_leido= temp[0];
					// analizar si es de estacionamiento de paquetes o de imagenes
					
				}

			}
			return tag_leido;

		}
	
	
	private NdefMessage[] getNdefMessages(Intent intent) {
		NdefMessage[] message = null;
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMessages != null) {
				message = new NdefMessage[rawMessages.length];
				for (int i = 0; i < rawMessages.length; i++) {
					message[i] = (NdefMessage) rawMessages[i];
				}
			} else {
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				message = new NdefMessage[] { msg };
			}
		} else {

			
		}
		return message;
	}
	
public String nombreTagRecibido(Intent intent){
	return escucharTag(intent);
	
}
	
}
