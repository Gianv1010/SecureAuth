import "./totp.css"
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";

function QRcode() {
  const [errorMsg, setErrorMsg] = useState("");

  const [code, setCode] = useState("");
  const location = useLocation();
  const navigate = useNavigate();
  const email = location.state?.email ?? "";

  const handleChange = (e) => {
    const val = e.target.value;
    if (/^\d*$/.test(val) && val.length <= 6) setCode(val);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (code.length !== 6) return;
  try {
    const response = await fetch("/api/auth/2fa/verify", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, code }),
    });

    const data = await response.json().catch(() => null);

    //if (!data) {
    //  setErrorMsg("Risposta non valida dal server");
    //  return;
    //}

    if (!response.ok || !data.success) {
      setErrorMsg(data.message ?? "Codice errato");
      return;
    }
    // ok
    navigate("/welcome", { replace: true });
  } catch (errorMsg) {
    setErrorMsg("Errore di rete");
  }
  };

  const handleRecovery = () => {
    navigate("/formRecovery", { state: { email } });
  };
  return (
    <div className="qr-page">
      <div className="qr-card">
        <h2 className="qr-title">Inserisci totp</h2>
        {/* Form sotto, separato: non deve comprimere il QR */}
        <form className="qr-form" onSubmit={handleSubmit}>
          <label className="qr-label" htmlFor="totpCode">Codice di verifica</label>

          <input
            id="totpCode"
            type="text"
            name="totpCode"
            placeholder="000000"
            value={code}
            onChange={handleChange}
            maxLength={6}
            inputMode="numeric"
            autoComplete="one-time-code"
            className="qr-input"
            required
          />


          <button className="qr-submit" type="submit" disabled={code.length !== 6}>
            Verifica codice
          </button>
            {errorMsg !== "" ? (
              <span className="error-text">{errorMsg}</span>
            ) : null}
          <button className="qr-recoveryCodes" type="button" onClick={handleRecovery}>
            Voglio utilizzare codice di backup
          </button>
        </form>

        <div className="qr-links">
          <Link className="qr-link" to="/login">Torna al Login</Link>
        </div>
      </div>
    </div>
  );
}

export default QRcode;
