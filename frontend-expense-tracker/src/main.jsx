import 'bootstrap/dist/css/bootstrap.min.css';
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App.jsx';
import AuthProvider from './context/AuthProvider.jsx';
import { UserProvider } from './context/UserProvider.jsx';
createRoot(document.getElementById('root')).render(
    <BrowserRouter>
    <AuthProvider>
       <UserProvider>
    <App />
  </UserProvider>
    </AuthProvider>
    </BrowserRouter>
)
