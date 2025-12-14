import { useMemo, useState } from "react";
import "./formRegister.css";
import {Link} from "react-router-dom"
 function FormRegister() {

   const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [password, setPassword] = useState("");
  const [isPasswordFocused, setIsPasswordFocused] = useState(false);

    // Requisiti password
  const checks = useMemo(() => {
    return {
      length: password.length >= 8,
      upper: /[A-Z]/.test(password),
      lower: /[a-z]/.test(password),
      number: /[0-9]/.test(password),
      special: /[^A-Za-z0-9]/.test(password), // qualsiasi carattere non alfanumerico
    };
  }, [password]);

    return(
    <>
    <div className='form'>
        <h2>Registrazione</h2>
        <label>Username</label>
        <input type="text" name="username" placeholder="Username..." maxLength={30} request></input>
        <label>Email</label>
        <input type="email" name="email" placeholder="Email..." request></input>
        <label className="labelRow">Password 
            <span className="eye" onClick={() => setShowPassword(!showPassword)}>{showPassword ? "🙊" : "🙈"}</span>
        </label>
        <input type={showPassword ? "text" : "password"} name="password" placeholder="Password..." minLength={8} maxLength={256}  onChange={(e) => setPassword(e.target.value)} onFocus={() => setIsPasswordFocused(true)} onBlur={() => setIsPasswordFocused(false)}request></input>
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
        <input type={showConfirmPassword ? "text" : "password"} name="confermaPassword" placeholder="Conferma password..." minLength={8} maxLength={256} request></input>
        
        <div className="checkedRow">
            <label className="switch">
                <input type="checkbox" name="twoFactor" />
                <span className="slider"></span>
            </label>
            <span className="switchLabel">2FA</span>
        </div>


        <input type="submit" value="Registrati"></input>
    <p><Link to={'/Login'}>Ho già un account</Link></p>

    </div>
    </>
    );
 }

 export default FormRegister;