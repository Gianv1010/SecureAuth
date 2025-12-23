import {useState} from "react";
import "./qrCode.css"
import qrcodeImage from "../../assets/paulo-dybala.png"
import { Link } from "react-router-dom";

function QRcode() {

    const [code, setCode] = useState("");

    const handleChange = (e) => {
        const val = e.target.value;
        // Regex 
        // ^ --> inizio stringa
        // \d --> solo numeri da 0 a 9
        // * --> zero o piu cifre
        // $ --> fine stringa
        if (/^\d*$/.test(val)) {
            // deve essere per forza uguale a 6
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
            <div className='containerQrcode'>
                <h2>Scansiona QR code</h2>
                
                {/* Contenitore per centrare l'immagine */}
                <div className="qr-image-wrapper">
                    <img src={qrcodeImage} alt="QR Code" className="qr-image" />
                </div>

                <form onSubmit={handleSubmit}>
                    <label>Codice di verifica</label>
                    <input type="text" name="totpCode" placeholder="000 000" value={code} onChange={handleChange} maxLength={6} inputMode="numeric" className="qr-code-input" required/>
                    <label className="secret" id="secret">secret</label>

                    <input className="qr-submit" type="submit" value="Verifica Codice" disabled={code.length !== 6}/>
                </form>

                <div className="auth-links">
                    <Link to="/login">Torna al Login</Link>
                </div>

            </div>
        </>
    );
}

export default QRcode;