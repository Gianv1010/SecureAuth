import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./recoveryCode.css";

export default function RecoveryCodes() {
  const location = useLocation(); // per leggere i dati che passo con navigate
  const navigate = useNavigate();

  // ?--> serve per non far crashare il frontend, se arriva null diventa semplicemente undefinited
  const recoveryCodes = location.state?.recoveryCodes ?? []; // ??--> se recoveryCodes è undefinited, utilizzo una lista vuota ("[]")
  const email = location.state?.email ?? "";

  const [copied, setCopied] = useState(false);
  const [copiedOne, setCopiedOne] = useState("");

  // se arrivi qui senza state (refresh/link), rimanda indietro
  useEffect(() => {
    if (!Array.isArray(recoveryCodes) || recoveryCodes.length === 0) {
      navigate("/login", { replace: true });
    }
  }, [recoveryCodes, navigate]);

  const formatted = useMemo(() => {
    return recoveryCodes.join("\n"); //aggiunge i vari elementi del recoveryCodes, e li mette uno sotto l'altro ("\n") senza frare ogni volta il re-rendering
  }, [recoveryCodes]);

  async function copyAll() {
    try {
      //serve per copiare il testo negli appunti del pc
      // await serve per dire aspetta che tutto il testo venga copiato
      await navigator.clipboard.writeText(formatted);
      setCopied(true);
      //dopo 1,5s il bottone per copiare il testo viene renderizzato e torna allo stato iniziale
      setTimeout(() => setCopied(false), 1500);
    } catch {
      // fallback vecchio ( per alcuni browser)
      const ta = document.createElement("textarea");
      ta.value = formatted;
      document.body.appendChild(ta);
      ta.select();
      document.execCommand("copy");
      document.body.removeChild(ta);
      setCopied(true);
      setTimeout(() => setCopied(false), 1500);
    }
  }

  async function copyOne(code) {
    try {
      await navigator.clipboard.writeText(code);
      setCopiedOne(code);
      setTimeout(() => setCopiedOne(""), 1200);
    } catch {
      // fallback
      const ta = document.createElement("textarea");
      ta.value = code;
      document.body.appendChild(ta);
      ta.select();
      document.execCommand("copy");
      document.body.removeChild(ta);
      setCopiedOne(code);
      setTimeout(() => setCopiedOne(""), 1200);
    }
  }

  function goHome() {
    navigate("/welcome");
  }

  //controllo se recoveryCodes contiene qualcosa, se non contineen nulla resituisco null, perchè significa che l'utente non dovrebbe trovarsi qui
  if (!Array.isArray(recoveryCodes) || recoveryCodes.length === 0) return null;

  return (
    <div className="rc-page">
      <div className="rc-card">
        <div className="rc-header">
        <div className="rc-header-text">
        <h2>Codici di backup</h2>
        <p className="rc-sub">
        Salvali in un posto sicuro. <b>Non verranno mostrati di nuovo</b>.
        </p>
        {email ? <p className="rc-email">Account: <b>{email}</b></p> : null}
    </div>

    <button className="rc-btn rc-btn-primary" onClick={copyAll}>
     {copied ? "Copiati ✅" : "Copia tutti"}
        </button>
</div>


        <div className="rc-box">
          <div className="rc-grid">
            {recoveryCodes.map((code, idx) => (
              <button
                type="button"
                key={`${code}-${idx}`}
                className="rc-code"
                onClick={() => copyOne(code)}
                title="Clicca per copiare"
              >
                <span className="rc-code-text">{code}</span>
                <span className="rc-code-hint">
                  {copiedOne === code ? "Copiato ✅" : "Copia"}
                </span>
              </button>
            ))}
          </div>

          <div className="rc-tip">
            Suggerimento: salva questi codici in un password manager o stampali e conservali offline.
          </div>
        </div>

        <div className="rc-footer">
          <button className="rc-btn rc-btn-ghost" onClick={goHome}>
            Vai alla pagina principale →
          </button>
        </div>
      </div>
    </div>
  );
}
