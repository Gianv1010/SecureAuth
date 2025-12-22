import { useState } from "react";
import "../FormRegister/formRegister.css";
import {Link} from "react-router-dom"

 function FormLogin() {

   const [showPassword, setShowPassword] = useState(false);

    return(
    <>
    <div className='form'>
        <h2>Login</h2>
        <label>Username o email</label>
        <input type="text" name="username" placeholder="Username o email..." maxLength={30} request></input>
        <label className="labelRow">Password 
            <span className="eye" onClick={() => setShowPassword(!showPassword)}>{showPassword ? "🙊" : "🙈"}</span>
        </label>
        <input type={showPassword ? "text" : "password"} name="password" placeholder="Password..." minLength={8} maxLength={256} request></input>


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