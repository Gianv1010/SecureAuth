package com.QrCode.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QrCodeGeneratorApplication 
{
	
	public static void main(String[] args) 
	{
		SpringApplication.run(QrCodeGeneratorApplication.class, args);
		
		GeneratoreQrCode generatore = new GeneratoreQrCode("C:/Users/Daniele/Downloads/QrCodeGenerati", 500, "jpg");
		
		try 
		{
			generatore.GeneraQrCode("Dati da inserire", "NomeFile");
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
