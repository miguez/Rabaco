package com.telventi.afirma.wsclient.utils;

public class BeanSalidaBrowser {

	String certificado;
	String firma;
	String pathDocumento;
	
	public String getCertificado() {
		return certificado;
	}
	public void setCertificado(String certificado) {
		this.certificado = certificado;
	}	
	
	public String getFirma() {
		return firma;
	}

	public void setFirma(String firma) {
		this.firma = firma;
	}
	
	public String getPathDocumento() {
		return pathDocumento;
	}
	
	public void setPathDocumento(String documento) {
		this.pathDocumento = documento;
		System.out.println("Path al documento--<" + this.pathDocumento);
	}
	
	
}
