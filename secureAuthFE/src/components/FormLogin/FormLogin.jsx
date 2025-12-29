import { useState } from "react";
import "../FormRegister/formRegister.css";
import { Link, useNavigate } from "react-router-dom";

export default function FormLogin() {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

    const [formData, setFormData] = useState({
    email: "",
    password: ""
  });

   function handleChange(e) {
    const { name, value} = e.target; //destrutturazione di un oggetto serve per prendere il name e il value degli input (di cui name diventa la chiave per prendere i value degli input)
                                      // equivalente: const name = e.target.name;
                                                    //const value = e.target.value;
    setFormData(prev => ({
      ...prev, //prende tutti i campi aggiunti precedentemente
      [name]: value 
    }));
  }

  async function handleSubmit(e) {
      e.preventDefault(); //evita il comportamento di default del browser, cioè il ricarcio della pagina => l'azzeramento degli state => perdita di dati nel form
  
      //JSON da mandare al backend
      const payload = {
        email: formData.email,
        password: formData.password
      };
  
      console.log("JSON inviato:", payload);
  
    try {
      const response = await fetch("/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
  
    // prova a leggere JSON, ma senza far crashare se non è JSON
  const data = await response.json().catch(() => null);
  if (!data) {
    setErrorMsg("Risposta non valida dal server");
    return;
  }
  if (!data.success) {
    setErrorMsg(data.message ?? "Errore durante la registrazione");
    return;
  }
  if (!response.ok) {
    setErrorMsg(data?.message ?? "Errore durante la registrazione");
    return;
  }
    if (data.enable2FA) {
      navigate("/totp", {state: {email : formData.email}});
    } else {
      navigate("/welcome");
    }
  } catch (err) {
    console.error("Errore:", err);
    setErrorMsg("Errore di rete");
  }
  }  

  return (
    <div className="form">
      <h2>Login</h2>

      <form onSubmit={handleSubmit}>
        <label>Email</label>
        <input
          type="text"
          name="email"
          placeholder="Email..."
          maxLength={100}
          onChange={handleChange}
          required
        />

        <label className="labelRow">
          Password
          <span className="eye" onClick={() => setShowPassword(!showPassword)}>
            {showPassword ? "🙊" : "🙈"}
          </span>
        </label>

        <input
          type={showPassword ? "text" : "password"}
          name="password"
          placeholder="Password..."
          minLength={8}
          maxLength={256}
          onChange={handleChange}
          required
        />

        {errorMsg ? <span className="error-text">{errorMsg}</span> : null}

        <input type="submit" value="accedi"/>

        <div className="auth-links">
          <Link to="/">Non ho un account</Link>
          <Link to="/">Password dimenticata</Link>
        </div>
      </form>
    </div>
  );
}
