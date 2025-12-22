import './App.css'
import Register from './pages/Register.jsx';
import Login from './pages/Login.jsx';
import Qrcode from './pages/Qrcode.jsx';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
function App() {

  return (

    <BrowserRouter>
    <Routes>
      <Route path="/register" element={<Register />} />

      <Route path="/login" element={<Login />} />
      
      <Route path="/qrcode" element={<Qrcode />} />

    </Routes>
  </BrowserRouter>
  );
}

export default App;