import React from 'react'
import './index.css'
import App from './App.jsx'
import ReactDOM from "react-dom/client"
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import Login from './pages/Login.jsx'
import QRcode from './pages/QRcode.jsx'
import Welcome from './pages/Welcome.jsx'
import RecoveryCode from './pages/RecoveryCodes.jsx'
import FormRecovery from './pages/FormRecovery.jsx'


const router = createBrowserRouter([
  {
    path: "/",
    element: <App></App>
  },
  {
    path: "/Login",
    element: <Login></Login>
  },
  {
    path: "/qrcode",
    element: <QRcode></QRcode>
  },
  {
    path: "/recoveryCodes",
    element: <RecoveryCode></RecoveryCode>
  },
  {
    path: "/formRecovery",
    element: <FormRecovery></FormRecovery>
  },
  {
    path: "/welcome",
    element: <Welcome></Welcome>
  },
]);

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <RouterProvider router={router}></RouterProvider>
  </React.StrictMode>
);


