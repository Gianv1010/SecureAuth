package com.QrCode.generator;

import java.io.IOException;
import java.nio.file.Paths;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class GeneratoreQrCode
{
	private String percorso;	//La cartella dove verranno salvati le immagini QrCode
	private int dimensione;		//Dimensione del QrCode
	private String formato;		//Tipologia del file: "jpg","png",ecc...
	
	public GeneratoreQrCode() //Un costruttore generico
	{
		
	}
	
	public GeneratoreQrCode(String percorso, int dimensione, String formato) //Il costruttore parametrizzato
	{
		this.percorso = percorso;
		this.dimensione = dimensione;
		this.formato = formato;
	}
	
	public void GeneraQrCode(String dato, String nomeFile) throws WriterException, IOException
	{
		percorso = percorso + "/" + nomeFile + "." + formato;
		BitMatrix matrix = new MultiFormatWriter().encode(dato, BarcodeFormat.QR_CODE, dimensione, dimensione); //Crea la matrice: converte il dato da stringa a QrCode
		MatrixToImageWriter.writeToPath(matrix, formato, Paths.get(percorso)); //Crea l'immagine del QrCode nella cartella selezionata
	}		
}
