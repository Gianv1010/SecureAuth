import { useState } from "react";
import "../FormRegister/formRegister.css";
import { Link} from "react-router-dom";

export default function FormEmail() {
  const [errorMsg, setErrorMsg] = useState("");

    const [formData, setFormData] = useState({
    email: "",
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
      };
  
      console.log("JSON inviato:", payload);
  
    try {
      const response = await fetch("/api/auth/forgot", {
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
  } catch (err) {
    console.error("Errore:", err);
    setErrorMsg("Errore di rete");
  }
  }  

  return (
    <div className="form">
      <h2>Inserisci email</h2>

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
            {errorMsg !== "" ? (
              <span className="error-text">{errorMsg}</span>
            ) : null}        
        <input type="submit" value="Invia email"/>
      </form>
    </div>
  );
}
