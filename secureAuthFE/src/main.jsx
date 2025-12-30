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
import TotpCode from './pages/totp.jsx'
import FormEmail from './pages/Email.jsx'
import FormResetPassword from './pages/FormResetPassword.jsx';

const router = createBrowserRouter([
  {
    path: "/",
    element: <App></App>
  },
  {
    path: "/login",
    element: <Login></Login>
  },
  {
    path: "/qrcode",
    element: <QRcode></QRcode>
  },
  {
    path: "/totp",
    element: <TotpCode></TotpCode>
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
    path: "/formEmail",
    element: <FormEmail></FormEmail>
  },
  {
    path: "/resetPassword",
    element: <FormResetPassword></FormResetPassword>
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


