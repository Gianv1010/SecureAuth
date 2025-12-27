import "./qrCode.css";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { QRCodeSVG } from "qrcode.react";
import { useEffect, useMemo, useState } from "react";

function QRcode() {
  const [errorMsg, setErrorMsg] = useState("");

  const [code, setCode] = useState("");
  const [copied, setCopied] = useState(false);

  const location = useLocation();
  const navigate = useNavigate();

  const secret = location.state?.secret ?? "";
  const qrCodeUri = location.state?.qrCodeUri ?? "";
  const email = location.state?.email ?? "";

  useEffect(() => {
    if (!secret || !qrCodeUri) {
      navigate("/register", { replace: true });
    }
  }, [secret, qrCodeUri, navigate]);

  const qrSize = useMemo(() => {
    // dimensione QR “sicura” per GA: non scendere troppo
    // puoi aumentare se vuoi (es. 440)
    return 380;
  }, []);

  const handleChange = (e) => {
    const val = e.target.value;
    if (/^\d*$/.test(val) && val.length <= 6) setCode(val);
  };

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(secret);
      setCopied(true);
      setTimeout(() => setCopied(false), 1200);
    } catch {
      // fallback: niente
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (code.length !== 6) return;
if (code.length !== 6) return;

  try {
    const response = await fetch("/api/auth/2fa/verify", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, code }),
    });

    const data = await response.json().catch(() => null);

    if (!data) {
      setErrorMsg("Risposta non valida dal server");
      return;
    }

    if (!response.ok || !data.success) {
      setErrorMsg(data.message ?? "Codice errato");
      return;
    }

    // ok
    navigate("/welcome");
  } catch (errorMsg) {
    setErrorMsg("Errore di rete");
  }
  };

  if (!secret || !qrCodeUri) return null;

  return (
    <div className="qr-page">
      <div className="qr-card">
        <h2 className="qr-title">Scansiona il QR code</h2>

        {email ? <p className="qr-subtitle">Account: <b>{email}</b></p> : null}

        {/* QR in un box dedicato, NON dentro al form */}
        <div className="qr-box" aria-label="QR Code">
          <div className="qr-box-inner">
            <QRCodeSVG
              value={qrCodeUri}
              size={qrSize}
              includeMargin
              bgColor="#ffffff"
              fgColor="#000000"
              level="L"
            />
          </div>
          <p className="qr-hint">
            Apri Google Authenticator → <b>+</b> → <b>Scansiona un codice QR</b>
          </p>
        </div>

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

          <div className="secret-row">
            <div className="secret-block">
              <code className="secret-value">{secret}</code>
            </div>

            <button
              type="button"
              className="copy-btn"
              onClick={handleCopy}
              title="Copia la secret"
            >
              {copied ? "Copiata" : "Copia"}
            </button>
          </div>

          <button className="qr-submit" type="submit" disabled={code.length !== 6}>
            Verifica codice
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
