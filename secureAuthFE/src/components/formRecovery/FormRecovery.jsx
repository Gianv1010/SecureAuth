import "./formRecovery.css";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";

export default function FormRecovery() {
  const location = useLocation();
  const navigate = useNavigate();

  const email = location.state?.email ?? "";

  const [recoveryCode, setRecoveryCode] = useState("");
  const [errorMsg, setErrorMsg] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  // Se arrivi qui senza email (refresh/link diretto), rimanda al login
  useEffect(() => {
    if (!email) {
      navigate("/login", { replace: true });
    }
  }, [email, navigate]);

  // Normalizza input: maiuscole e niente spazi
  const normalized = useMemo(() => {
    return recoveryCode.toUpperCase().replace(/\s+/g, "");
  }, [recoveryCode]);

  // Codici tipo: 12 caratteri (es: A1B2C3D4E5F6)
  const isValidFormat = useMemo(() => {
    return /^[A-Z0-9]{12}$/.test(normalized);
  }, [normalized]);

  const handleChange = (e) => {
    // permettiamo comunque di digitare, poi normalizziamo in display
    setRecoveryCode(e.target.value);
    if (errorMsg) setErrorMsg("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email) return;

    if (!isValidFormat) {
      setErrorMsg("Inserisci un recovery code valido (12 caratteri).");
      return;
    }

    if (isLoading) return;
    setIsLoading(true);
    setErrorMsg("");

    try {
      const response = await fetch("/api/auth/2fa/recovery/verify", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, recoveryCode: normalized }), // è come scrivere email: "email" 
                                                                            //code: "codiceNormalizzato" --> sintassi chiave valore --> per JSON
      });

      const data = await response.json().catch(() => null);

      if (!data) {
        setErrorMsg("Risposta non valida dal server");
        return;
      }

      if (!response.ok || !data.success) {
        setErrorMsg(data.message ?? "Recovery code non valido");
        return;
      }

      navigate("/welcome", { replace: true});
    } catch (err) {
      console.error(err);
      setErrorMsg("Errore di rete");
    } finally {
      setIsLoading(false);
    }
  };

  if (!email) return null;

  return (
    <div className="fr-page">
      <div className="fr-card">
        <div className="fr-header">
          <h2 className="fr-title">Accedi con Recovery Code</h2>
          <p className="fr-subtitle">
            Usa uno dei codici di backup generati durante l’attivazione della 2FA.
          </p>

        </div>

        <form className="fr-form" onSubmit={handleSubmit}>
          <label className="fr-label" htmlFor="recoveryCode">
            Recovery code
          </label>

          <input
            id="recoveryCode"
            type="text"
            name="recoveryCode"
            placeholder="ES: A1B2C3D4E5F6"
            value={normalized}
            onChange={handleChange}
            autoComplete="one-time-code"
            className={`fr-input ${errorMsg ? "fr-input-error" : ""}`}
            maxLength={24} 
            inputMode="text"
            required
          />

          {errorMsg ? <div className="fr-error">{errorMsg}</div> : null}

          <button
            className="fr-submit"
            type="submit"
            disabled={isLoading || !isValidFormat}
            title={!isValidFormat ? "Inserisci 12 caratteri (A-Z, 0-9)" : "Verifica"}
          >
            {isLoading ? "Verifica..." : "Verifica Recovery Code"}
          </button>

          <div className="fr-actions">
            <Link className="fr-link" to="/login">
              Torna al Login
            </Link>
            <button
              type="button"
              className="fr-link-btn"
              onClick={() => navigate("/qrcode", { replace: true })}
              title="Torna alla verifica con Authenticator"
            >
              Torna al codice Authenticator
            </button>
          </div>
        </form>

        <div className="fr-footer">
          <p className="fr-note">
            Ogni recovery code può essere usato <b>una sola volta</b>.
          </p>
        </div>
      </div>
    </div>
  );
}
