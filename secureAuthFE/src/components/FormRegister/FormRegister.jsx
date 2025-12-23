import { useMemo, useState } from "react"; //serve per: 1. calcolare un valore. 2.salvarlo in memoria. 3. ricalcolarlo solo quando cambiano alcune dipendenze
import "./formRegister.css";
import {Link} from "react-router-dom"
import { useNavigate } from "react-router-dom";

 function FormRegister() {
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  //const [password, setPassword] = useState("");
  const [isPasswordFocused, setIsPasswordFocused] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");
  // state per l'invio del form
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
    enable2FA: false
  });

   function handleChange(e) {
    const { name, value, type, checked } = e.target; //destrutturazione di un oggetto serve per prendere il name e il value degli input (di cui name diventa la chiave per prendere i value degli input)
                                      // equivalente: const name = e.target.name;
                                                    //const value = e.target.value;
    setFormData(prev => ({
      ...prev, //prende tutti i campi aggiunti precedentemente
      [name]: type === "checkbox" ? checked : value //ci inserisce quello per cui è stato chiamata la funzione, di cui name è il name dell'input e value il valore inserito
    }));
  }

  async function handleSubmit(e) {
    e.preventDefault(); //evita il comportamento di default del browser, cioè il ricarcio della pagina => l'azzeramento degli state => perdita di dati nel form

    //JSON da mandare al backend
    const payload = {
      username: formData.username,
      email: formData.email,
      password: formData.password,
      confirmPassword: formData.confirmPassword,
      enable2FA: formData.enable2FA
    };

    console.log("JSON inviato:", payload);

    // esempio fetch
    try {
      const response = await fetch("/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      const data = await response.json();
      console.log("Risposta backend:", data);
      if (!response.ok) // <-- status 400, 401, ecc.
      {            
        setErrorMsg(data.message);   // <-- prendO il messaggio dal backend
        return;
      }
      // SUCCESSO
      navigate("/login");

    } catch (err) {
      console.error("Errore:", err);
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
        <h2>Registrazione</h2>

        <form onSubmit={handleSubmit}>

        <label>Username</label>
        <input type="text" name="username" placeholder="Username..." onChange={handleChange} maxLength={30} required></input>

        <label>Email</label>
        <input type="email" name="email" placeholder="Email..." onChange={handleChange} required></input>
        
        <label className="labelRow">Password 
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

        <label className="labelRow">Conferma password 
            <span className="eye" onClick={() => setShowConfirmPassword(!showConfirmPassword)}>{showConfirmPassword ? "🙊" : "🙈"}</span>
        </label>
        <input type={showConfirmPassword ? "text" : "password"} name="confirmPassword" placeholder="Conferma password..." onChange={handleChange} minLength={8} maxLength={256} required></input>
        
        <div className="checkedRow">
            <label className="switch">
                <input type="checkbox" name="enable2FA" onChange={handleChange}/>
                <span className="slider"></span>
            </label>
            <span className="switchLabel">2FA</span>
        </div>
            {errorMsg !== "" ? (
              <span className="error-text">{errorMsg}</span>
            ) : null}
        <input type="submit" value="Registrati"></input>
        
        </form>
        
        <p><Link to={'/Login'}>Ho già un account</Link></p>

    </div>
    </>
    );
 }

 export default FormRegister;