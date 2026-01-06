import { useState } from "react";
import "../FormRegister/formRegister.css";

export default function FormEmail() {
  const [formData, setFormData] = useState({ email: "" });
  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errorMsg) setErrorMsg("");
    if (successMsg) setSuccessMsg("");
  }

  async function handleSubmit(e) {
    e.preventDefault();
    if (isLoading) return;

    setErrorMsg("");
    setSuccessMsg("");
    setIsLoading(true);

    try {
      const response = await fetch("/api/auth/forgot", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: formData.email.trim() }),
      });

      const data = await response.json().catch(() => null);

      if (!data) {
        setErrorMsg("Risposta non valida dal server");
        return;
      }

      if (!response.ok) {
        setErrorMsg(data.message ?? "Errore");
        return;
      }

      if (!data.success) {
        setErrorMsg(data.message ?? "Errore");
        return;
      }

      //mostra conferma all’utente (tipico per forgot password)
      setSuccessMsg(data.message ?? "Se l’email è registrata, ti abbiamo inviato un link.");
    } catch (err) {
      console.error(err);
      setErrorMsg("Errore di rete");
    }finally{
        setIsLoading(false);
    }
  }

  return (
    <div className="form">
      <h2>Inserisci email</h2>

      <form onSubmit={handleSubmit}>
        <label>Email</label>
        <input
          type="email"
          name="email"
          placeholder="Email..."
          maxLength={100}
          value={formData.email}
          onChange={handleChange}
          required
        />

        {errorMsg ? <div className="error-text">{errorMsg}</div> : null}
        {successMsg ? <div className="success-text">{successMsg}</div> : null}

        <input type="submit" value={isLoading ? "Invio..." : "Invia email"} disabled={isLoading} />
      </form>
    </div>
  );
}
