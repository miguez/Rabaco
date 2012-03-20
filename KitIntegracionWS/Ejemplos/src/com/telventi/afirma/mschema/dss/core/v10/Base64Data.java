package com.telventi.afirma.mschema.dss.core.v10;

import java.lang.reflect.InvocationTargetException;

import com.telventi.afirma.mschema.AElement;

public class Base64Data extends AElement {
	
	private byte[] data = null;
	
	private String mimeType = null;

	

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public static void main (String[] args){
		Base64Data b = new Base64Data();
		try {
			b.getClass().getMethod("setData",new Class[]{byte[].class}).invoke(b,new Object[]{"misdatos".getBytes()});
			System.out.println(new String(b.getData()));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
