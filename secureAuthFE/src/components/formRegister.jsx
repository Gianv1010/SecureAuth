import "./formRegister.css";
 function FormRegister() {

    return(
    <>
    <div className='form'>
        <h2>Registrazione</h2>
        <label>Username</label>
        <input type="text" name="username" placeholder="Username..."></input>
        <label>Email</label>
        <input type="email" name="email" placeholder="Email..." ></input>
        <label>Password</label>
        <input type="password" name="password" placeholder="Password..."></input>
        <label>Conferma password</label>
        <input type="password" name="confermaPassword" placeholder="Conferma password..."></input>
        
        <div className="checkedRow">
            <input type="checkbox"></input> 
            <label>2FA</label>
        </div>

        <input type="submit" value="Registrati"></input>


    </div>
    </>
    );
 }

 export default FormRegister;