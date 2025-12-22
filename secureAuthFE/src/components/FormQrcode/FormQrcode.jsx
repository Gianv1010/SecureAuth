import {useState} from "react";
import "../FormQrcode/FormQrcode.css"
import qrcodeImage from "../../assets/paulo-dybala.png"
import { Link } from "react-router-dom";

    function FormQrcode() {

        const [code, setCode] = useState("");

        const handleChange = (e) => {
        const val = e.target.value;
        // Regex /^\d*$/ significa: accetta solo cifre da 0 a 9
        if (/^\d*$/.test(val)) {
            if (val.length <= 6) {
                setCode(val);
            }
        }
    };  
    
    const handleSubmit = (e) => {
        e.preventDefault(); // Evita che la pagina si ricarichi
        if (code.length === 6) {
            alert("Codice inviato correttamente: " + code);
        } else {
            alert("Il codice deve essere di 6 cifre.");
        }
    };
    return (
        <>
            <div className='form'>
                <h2>Autenticazione 2FA</h2>
                
                {/* Testo di istruzioni */}
                <p style={{ textAlign: 'center', fontSize: '0.9rem', marginBottom: '15px' }}>
                    Scansiona il QR Code con la tua app Authenticator.
                </p>
                
                {/* Contenitore per centrare l'immagine */}
                <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '20px' }}>
                    <img 
                        src={qrcodeImage} 
                        alt="QR Code" 
                        style={{ 
                            width: '150px', 
                            height: '150px',
                            border: '1px solid #ddd', 
                            padding: '5px',
                            borderRadius: '8px',
                            background: 'white'
                        }}
                    />
                </div>
                <form onSubmit={handleSubmit}>
                    <label>Codice di verifica</label>
                    <input 
                        type="text" 
                        name="totpCode" 
                        placeholder="000 000" 
                        value={code}
                        onChange={handleChange}
                        maxLength={6}
                        inputMode="numeric" // Apre il tastierino numerico su mobile
                        required
                        // Stile specifico per il codice: testo centrato e spaziato
                        style={{ 
                            textAlign: 'center', 
                            letterSpacing: '0.3rem', 
                            fontSize: '1.2rem',
                            fontWeight: 'bold'
                        }}
                    />
                    <input 
                        type="submit" 
                        value="Verifica Codice" 
                        // Il bottone appare "spento" (opacity) se non ci sono 6 numeri
                        style={{ opacity: code.length === 6 ? 1 : 0.6 }}
                        disabled={code.length !== 6}
                    />
                </form>

                <div className="auth-links">
                    <Link to="/login">Torna al Login</Link>
                </div>

            </div>
        </>
    );
}

export default FormQrcode;