package com.telventi.afirma.wsclient.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jdesktop.jdic.browser.WebBrowser;
import org.jdesktop.jdic.browser.internal.WebBrowserUtil;

/**
 * JDIC API demo main class.
 * <p>
 * SimpleBrowser demonstrate the usage of JDIC API package
 * org.jdesktop.jdic.browser (Browser component).
 */

/**
 * @author Administrador
 * 
 */
public class BrowserAfirma {

	static int count = 0;

	static BeanSalidaBrowser salida = new BeanSalidaBrowser();
	
	private static int INIT_SLEEP_TIME = 3000; 

	/**
	 * @param datos
	 *            Datos a firmar, pueden ser datos o hash
	 * @param tipo
	 *            0 Datos, 1 Hash
	 * @param formatoFirma
	 *            Formato de la firma
	 * @param algoritmoHash
	 *            Algoritmo de Hash
	 * @return Firma y certificado
	 */
	public static BeanSalidaBrowser navegar(String datos, int tipo,
			String formatoFirma, String algoritmoHash) {

		WebBrowser.setDebug(false);
		JFrame frame = new JFrame("@firma5");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final WebBrowser webBrowser = new WebBrowser();

		try {

			File clienteFile = new File("Cliente/Prueba.html");
			webBrowser
					.setURL(new URL("file://" + clienteFile.getAbsolutePath()));

		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			return null;
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(100, 100));
		panel.add(webBrowser, BorderLayout.CENTER);

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

		try {
			Thread.sleep(INIT_SLEEP_TIME);
			long tiempoInicial, tiempo = 0;

			tiempoInicial = System.currentTimeMillis();
			//Si es mozilla tenemos que esperar 20seg para que no se pierda la conexion browser
			if (WebBrowserUtil.isDefaultBrowserMozilla())
				Thread.sleep(20000);
			
			while ((!webBrowser.executeScript("isCargado()").trim().equals(
					"true"))
					&& (tiempo < 20000)) {
				Thread.sleep(200);
				tiempo = System.currentTimeMillis() - tiempoInicial;
			}
			datos = datos.replaceAll("\\n", "\\n");
			// Se firman datos
			if (tipo == 0) {
				tiempoInicial = System.currentTimeMillis();
				salida.setFirma(webBrowser.executeScript("firmaDatosBase64('"
						+ datos + "','" + formatoFirma + "','" + algoritmoHash
						+ "')"));
				while ((!webBrowser.executeScript("isFirmadoOError()").trim()
						.equals("true"))
						&& (tiempo < 20000)) {
					Thread.sleep(200);
					tiempo = System.currentTimeMillis() - tiempoInicial;
				}
			}

			// Se firma hash
			if (tipo == 1) {
				tiempoInicial = System.currentTimeMillis();
				
				// Por el momento firmamos el hash como si fueran datos: Problema del formato de firma XADES.
				if (formatoFirma.equalsIgnoreCase("XADES") || formatoFirma.equalsIgnoreCase("XADES-BES") || formatoFirma.equalsIgnoreCase("XMLDSIG") || formatoFirma.equalsIgnoreCase("XMLDSIGN")) {
					System.err.println("************** BrowserAfirma.java: REALMENTE PARA XADES Y XMLDSIG SE FIRMA EL HASH COMO DATOS ********************");
					salida.setFirma(webBrowser.executeScript("firmaDatosBase64('"
							+ datos + "','" + formatoFirma + "','" + algoritmoHash
							+ "')"));
				} else {
					salida.setFirma(webBrowser.executeScript("firmarHashEnBase64('"
							+ datos + "','" + formatoFirma + "','" + algoritmoHash
							+ "')"));
				}
				while ((!webBrowser.executeScript("isFirmadoOError()").trim()
						.equals("true"))
						&& (tiempo < 20000)) {
					Thread.sleep(200);
					tiempo = System.currentTimeMillis() - tiempoInicial;
				}
			}

			salida
					.setCertificado(webBrowser
							.executeScript("dameUltimoCertificadoUsadoParaFirmarCodificadoEnBase64()"));
			
			Thread.sleep(1000);
			
			webBrowser.stop();
			
			frame.setVisible(false);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return salida;

	}

	/**
	 * @param datos
	 *            Datos a firmar, pueden ser datos o hash, con el algoritmo de
	 *            hash por defecto
	 * @param tipo
	 *            0 Datos, 1 Hash
	 * @param formatoFirma
	 *            Formato de la firma
	 * @return Firma y certificado
	 */
	public static BeanSalidaBrowser navegar(String datos, int tipo,
			String formatoFirma) {

		WebBrowser.setDebug(false);
		JFrame frame = new JFrame("@firma5");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final WebBrowser webBrowser = new WebBrowser();

		try {

			File clienteFile = new File("Cliente/Prueba.html");
			webBrowser
					.setURL(new URL("file://" + clienteFile.getAbsolutePath()));

		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			return null;
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(100, 100));
		panel.add(webBrowser, BorderLayout.CENTER);

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

		try {
			Thread.sleep(INIT_SLEEP_TIME);
			long tiempoInicial, tiempo = 0;

			tiempoInicial = System.currentTimeMillis();
			
			//Si es mozilla tenemos que esperar 20seg para que no se pierda la conexion browser
			if (WebBrowserUtil.isDefaultBrowserMozilla())
				Thread.sleep(20000);
			
			while ((!webBrowser.executeScript("isCargado()").trim().equals(
					"true"))
					&& (tiempo < 20000)) {
				Thread.sleep(200);
				tiempo = System.currentTimeMillis() - tiempoInicial;
			}
			datos = datos.replaceAll("\\n", "\\n");
			// Se firman datos
			if (tipo == 0) {
				tiempoInicial = System.currentTimeMillis();
				salida.setFirma(webBrowser
						.executeScript("firmaDatosBase64Sin('" + datos + "','"
								+ formatoFirma + "')"));
				while ((!webBrowser.executeScript("isFirmadoOError()").trim()
						.equals("true"))
						&& (tiempo < 20000)) {
					Thread.sleep(200);
					tiempo = System.currentTimeMillis() - tiempoInicial;
				}
			}

			// Se firma hash
			if (tipo == 1) {
				tiempoInicial = System.currentTimeMillis();
				salida.setFirma(webBrowser
						.executeScript("firmarHashEnBase64Sin('" + datos
								+ "','" + formatoFirma + "')"));
				while ((!webBrowser.executeScript("isFirmadoOError()").trim()
						.equals("true"))
						&& (tiempo < 20000)) {
					Thread.sleep(200);
					tiempo = System.currentTimeMillis() - tiempoInicial;
				}
			}

			salida
					.setCertificado(webBrowser
							.executeScript("dameUltimoCertificadoUsadoParaFirmarCodificadoEnBase64()"));
			Thread.sleep(1000);
			
			webBrowser.stop();
			
			frame.setVisible(false);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return salida;

	}

	/**
	 * @param firmaUri
	 *            Path al fichero que contiene la firma electronica
	 * @param hash
	 *            Hash
	 * @param formatoFirma
	 *            Formato de la firma
	 * @param algoritmoHash
	 *            Algoritmo de Hash
	 * @return Firma y certificado
	 */
	public static BeanSalidaBrowser navegarCoSign(String firmaUri, String hash,
			String formatoFirma, String algoritmoHash) {

		WebBrowser.setDebug(false);
		JFrame frame = new JFrame("@firma5");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final WebBrowser webBrowser = new WebBrowser();

		try {

			File clienteFile = new File("Cliente/Prueba.html");
			webBrowser
					.setURL(new URL("file://" + clienteFile.getAbsolutePath()));

		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			return null;
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(100, 100));
		panel.add(webBrowser, BorderLayout.CENTER);

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		try {
			Thread.sleep(INIT_SLEEP_TIME);
			long tiempoInicial, tiempo = 0;

			tiempoInicial = System.currentTimeMillis();
			
			//Si es mozilla tenemos que esperar 20seg para que no se pierda la conexion browser
			if (WebBrowserUtil.isDefaultBrowserMozilla())
				Thread.sleep(20000);
			
			while ((!webBrowser.executeScript("isCargado()").trim().equals(
					"true"))
					&& (tiempo < 20000)) {
				Thread.sleep(200);
				tiempo = System.currentTimeMillis() - tiempoInicial;
			}


			// Se realiza la firma CoSign
			tiempoInicial = System.currentTimeMillis();
			salida.setFirma(webBrowser.executeScript("firmaCoSignBase64('" + firmaUri
					+ "','" + hash + "','" + formatoFirma + "','"
					+ algoritmoHash + "')"));
			while ((!webBrowser.executeScript("isFirmadoOError()").trim()
					.equals("true"))
					&& (tiempo < 20000)) {
				Thread.sleep(200);
				tiempo = System.currentTimeMillis() - tiempoInicial;
			}

			salida
					.setCertificado(webBrowser
							.executeScript("dameUltimoCertificadoUsadoParaFirmarCodificadoEnBase64()"));
			
			Thread.sleep(1000);
			
			webBrowser.stop();
			
			frame.setVisible(false);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return salida;

	}
	
	/**
	 * @param firmaUri
	 *            Path al fichero que contiene la firma electronica
	 * @param formatoFirma
	 *            Formato de la firma
	 * @return Firma y certificado
	 */
	public static BeanSalidaBrowser navegarCounterSign(String firmaUri, 
			String formatoFirma) {

		WebBrowser.setDebug(false);
		JFrame frame = new JFrame("@firma5");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final WebBrowser webBrowser = new WebBrowser();

		try {

			File clienteFile = new File("Cliente/Prueba.html");
			webBrowser
					.setURL(new URL("file://" + clienteFile.getAbsolutePath()));

		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			return null;
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(100, 100));
		panel.add(webBrowser, BorderLayout.CENTER);

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		try {
			Thread.sleep(INIT_SLEEP_TIME);
			long tiempoInicial, tiempo = 0;

			tiempoInicial = System.currentTimeMillis();
			
			//Si es mozilla tenemos que esperar 20seg para que no se pierda la conexion browser
			if (WebBrowserUtil.isDefaultBrowserMozilla())
				Thread.sleep(20000);
			
			while ((!webBrowser.executeScript("isCargado()").trim().equals(
					"true"))
					&& (tiempo < 20000)) {
				Thread.sleep(200);
				tiempo = System.currentTimeMillis() - tiempoInicial;
			}


			// Se realiza la firma CoSign
			tiempoInicial = System.currentTimeMillis();
			salida.setFirma(webBrowser.executeScript("firmaCounterSignBase64('" + firmaUri
					+ "','" + formatoFirma + "')"));
			while ((!webBrowser.executeScript("isFirmadoOError()").trim()
					.equals("true"))
					&& (tiempo < 20000)) {
				Thread.sleep(200);
				tiempo = System.currentTimeMillis() - tiempoInicial;
			}

			salida
					.setCertificado(webBrowser
							.executeScript("dameUltimoCertificadoUsadoParaFirmarCodificadoEnBase64()"));
			
			Thread.sleep(1000);
			
			webBrowser.stop();
			
			frame.setVisible(false);
		
			Thread.sleep(3000);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return salida;

	}
	
	/**
	 * @param formatoFirma
	 *            Formato de la firma
	 * @param algoritmoHash
	 * 			  Algoritmo de Hash
	 * @return Firma, certificado y path del documento firmado
	 */
	public static BeanSalidaBrowser navegar(String formatoFirma, String algorimoHash) {

		WebBrowser.setDebug(false);
		JFrame frame = new JFrame("@firma5");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final WebBrowser webBrowser = new WebBrowser();

		try {

			File clienteFile = new File("Cliente/Prueba.html");
			webBrowser
					.setURL(new URL("file://" + clienteFile.getAbsolutePath()));

		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			return null;
		}


		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(100, 100));
		panel.add(webBrowser, BorderLayout.CENTER);

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

		try {
			Thread.sleep(INIT_SLEEP_TIME);
			long tiempoInicial, tiempo = 0;

			tiempoInicial = System.currentTimeMillis();
			
			//Si es mozilla tenemos que esperar 20seg para que no se pierda la conexion browser
			if (WebBrowserUtil.isDefaultBrowserMozilla())
				Thread.sleep(20000);
			
			while ((!webBrowser.executeScript("isCargado()").trim().equals(
					"true"))
					&& (tiempo < 20000)) {
				Thread.sleep(200);
				tiempo = System.currentTimeMillis() - tiempoInicial;
			}

			tiempoInicial = System.currentTimeMillis();
			salida.setFirma(webBrowser
					.executeScript("firma('"+ formatoFirma + "','" + algorimoHash + "')"));
			while ((!webBrowser.executeScript("isFirmadoOError()").trim()
					.equals("true"))
					&& (tiempo < 20000)) {
				Thread.sleep(200);
				tiempo = System.currentTimeMillis() - tiempoInicial;
			}

			salida
				.setCertificado(webBrowser
					.executeScript("dameUltimoCertificadoUsadoParaFirmarCodificadoEnBase64()"));
			
			Thread.sleep(1000);

			salida
					.setPathDocumento(webBrowser
							.executeScript("damePathUltimoDocumentoFirmado()"));
			
			Thread.sleep(1000);
			
			webBrowser.stop();
			
			frame.setVisible(false);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return salida;

	}

}
