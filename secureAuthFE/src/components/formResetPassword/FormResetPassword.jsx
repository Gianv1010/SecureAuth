import { useMemo, useState } from "react"; //serve per: 1. calcolare un valore. 2.salvarlo in memoria. 3. ricalcolarlo solo quando cambiano alcune dipendenze
import "../FormRegister/formRegister.css";
import { useNavigate } from "react-router-dom";

 function FormResetPassword() {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  //const [password, setPassword] = useState("");
  const [isPasswordFocused, setIsPasswordFocused] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  // state per l'invio del form
  const urlParams = new URLSearchParams(window.location.search);
  const tokenPlain = urlParams.get("token"); //prendo il tokenPlain dall'url

  const [formData, setFormData] = useState({
    password: "",
    confirmPassword: "",
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
      token: tokenPlain,
      newPassword: formData.password,
      confirmPassword: formData.confirmPassword,
    };

    console.log("JSON inviato:", payload);

   try {
  const response = await fetch("/api/auth/reset", {
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
  setErrorMsg(data.message ?? "Errore");
  return;
}
if (!response.ok) {
  setErrorMsg(data?.message ?? "Errore");
  return;
}
navigate("/login");

} catch (err) {
  console.error("Errore:", err);
  setErrorMsg("Errore di rete");
}
}

    // Requisiti password
  const checks = useMemo(() => { 
    const password = formData.password;
    return {
      length: password.length >= 8,
      upper: /[A-Z]/.test(password),
      lower: /[a-z]/.test(password),
      number: /[0-9]/.test(password),
      special: /[^A-Za-z0-9]/.test(password), // qualsiasi carattere non alfanumerico
    };
  }, [formData.password]); //valore che deve cambiare per attivare useMemo

    return(
    <>
    <div className='form'>
        <h2>Reset Password</h2>

        <form onSubmit={handleSubmit}>
        
        <label className="labelRow">Nuova password 
            <span className="eye" onClick={() => setShowPassword(!showPassword)}>{showPassword ? "🙊" : "🙈"}</span>
        </label>
        <input type={showPassword ? "text" : "password"} name="password" placeholder="Password..." onChange={handleChange} minLength={8} maxLength={256}  onFocus={() => setIsPasswordFocused(true)} onBlur={() => setIsPasswordFocused(false)} required></input>
            {/* BOX REQUISITI (si apre mentre scrive/focus) */}
            {isPasswordFocused && (
                <div className="passwordHint">
                  <p className="hintTitle">La password deve contenere:</p>
                    <ul className="hintList">
                      <li className={checks.length ? "ok" : "bad"}>Almeno 8 caratteri</li>
                      <li className={checks.upper ? "ok" : "bad"}>Almeno 1 lettera maiuscola (A-Z)</li>
                      <li className={checks.lower ? "ok" : "bad"}>Almeno 1 lettera minuscola (a-z)</li>
                      <li className={checks.number ? "ok" : "bad"}>Almeno 1 numero (0-9)</li>
                      <li className={checks.special ? "ok" : "bad"}>Almeno 1 carattere speciale (es. ! @ # ?)</li>
                    </ul>
                </div>
              )}

        <label className="labelRow">Conferma nuova password 
            <span className="eye" onClick={() => setShowConfirmPassword(!showConfirmPassword)}>{showConfirmPassword ? "🙊" : "🙈"}</span>
        </label>
        <input type={showConfirmPassword ? "text" : "password"} name="confirmPassword" placeholder="Conferma password..." onChange={handleChange} minLength={8} maxLength={256} required></input>
        
            {errorMsg !== "" ? (
              <span className="error-text">{errorMsg}</span>
            ) : null}
        <input type="submit" value="Cambia Password"></input>
        
        </form>
    </div>
    </>
    );
 }

 export default FormResetPassword;