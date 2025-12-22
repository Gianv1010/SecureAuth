package com.secureAuthLDC.secureAuthBE.security;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class CryptoService {

  private final Aead aead;
  //prendo la keyset in base64 dalle variabili d'ambiente
  public CryptoService(@Value("${security.tink.keyset-b64}") String keysetB64) throws Exception {
	  //inizializzo algoritmi AEAD
	  AeadConfig.register();
	//decodifico il base64 preso dalle variabili d'ambiente in formato byte
    byte[] keysetJsonBytes = Base64.getDecoder().decode(keysetB64);
    //permette di leggere i byte come stream (flusso di byte contigui)
    //CleartextKeysetHandle.read --> crea il KeysetHandle
    KeysetHandle handle = CleartextKeysetHandle.read(
    	//legge il json (JsonKeysetReader)
    	JsonKeysetReader.withInputStream(new ByteArrayInputStream(keysetJsonBytes))
    );
    //prendo la primarykey dal keyset decifrato, ma io non ho accesso diretto alla chiave, non la utilizzo io direttamente
    //riesco ad utilizzarla grazie a tink che la gestisce e mi permette di utilizzarla per cifrare e decifrare
    this.aead = handle.getPrimitive(Aead.class);
  }

  //associatedData serve per autenticare il chiperText, per dire a chi appartiene, posso passare username o email
  //e se un hacker prende il chipertext non può usarlo su un altro utente in quanto è autenticato dall'associatedData
  //nel DB salvo il chipertext + TAG (tag formato da chipertext+associtedData+chiave+nonce(stringa casuale salvata nel pacchetto dati che viene cifrato))
  public String encryptToBase64(String plaintext, Long associatedData) throws Exception {
    byte[] ct = aead.encrypt(
    	//converto la stringa che passo (la secret in questo caso) in Byte
        plaintext.getBytes(StandardCharsets.UTF_8),
        //converto associatedData da Long a String in quanto il metodo getBytes vuole una String per convertire in byte
        associatedData == null ? new byte[0] : associatedData.toString().getBytes(StandardCharsets.UTF_8)
    );
    //restituisce i byte codificati in formato stringa con Base64
    return Base64.getEncoder().encodeToString(ct);
  }

  public String decryptFromBase64(String ciphertextB64, Long associatedData) throws Exception {
    //decripta il chipertext da base64 e ottiene il chipetext + tag
	byte[] ct = Base64.getDecoder().decode(ciphertextB64);
    //esegue criptazione dei byte ottenuti
	byte[] pt = aead.decrypt(
        ct,
        //converto associatedData da Long a String in quanto il metodo getBytes vuole una String per convertire in byte
        associatedData == null ? new byte[0] : associatedData.toString().getBytes(StandardCharsets.UTF_8)
    );
    return new String(pt, StandardCharsets.UTF_8);
  }
}
