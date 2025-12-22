package com.secureAuthLDC.secureAuthBE.security;

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.CleartextKeysetHandle;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

//classe java standalone, serve solo per generare il keyset
public class KeysetGenerator {
  public static void main(String[] args) throws Exception {
    //inizializzo Tink per utilizzare AES-GCM
	  AeadConfig.register();

	//genero la chiave simmetrica AES-256, la inserisco in un keyset, la marco ome primary e assegno un keyID
    KeysetHandle handle = KeysetHandle.generateNew(AeadKeyTemplates.AES256_GCM);
    //scrivo il keyset in formato JSON
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    //il keyset viene convertito in json
    CleartextKeysetHandle.write(handle, JsonKeysetWriter.withOutputStream(out));
    //trasformo il json in base64 per poter memorizzare la chiave nel .env
    String keysetB64 = Base64.getEncoder().encodeToString(out.toByteArray());
    //stampo il keyset
    System.out.println("TINK_KEYSET_B64=");
    System.out.println(keysetB64);
  }
}
