import { useState } from "react";
import "../FormRegister/formRegister.css";
import {Link} from "react-router-dom"

 function FormLogin() {

   const [showPassword, setShowPassword] = useState(false);

  // state per l'invio del form
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
  });

  function handleChange(e) {
    const { name, value, type } = e.target; //destrutturazione di un oggetto serve per prendere il name e il value degli input (di cui name diventa la chiave per prendere i value degli input)
                                      // equivalente: const name = e.target.name;
                                                    //const value = e.target.value;
    setFormData(prev => ({
      ...prev, //prende tutti i campi aggiunti precedentemente
      [name]: type = value //ci inserisce quello per cui è stato chiamata la funzione, di cui name è il name dell'input e value il valore inserito
    }));
  }
    return(
    <>
    <div className='form'>
        <h2>Login</h2>
        <label>Username o email</label>
        <input type="text" name="username" placeholder="Username o email..." maxLength={30} required></input>
        <label className="labelRow">Password 
            <span className="eye" onClick={() => setShowPassword(!showPassword)}>{showPassword ? "🙊" : "🙈"}</span>
        </label>
        <input type={showPassword ? "text" : "password"} name="password" placeholder="Password..." minLength={8} maxLength={256} required></input>


        <input type="submit" value="Accedi"></input>
<div className="auth-links">
  <Link to="/">Non ho un account</Link>
  <Link to="/">Password dimenticata</Link>
</div>

    </div>
    </>
    );
 }

 export default FormLogin;