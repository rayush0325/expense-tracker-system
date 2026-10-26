import 'bootstrap/dist/css/bootstrap.min.css';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App.jsx';
import AuthProvider from './context/AuthProvider.jsx';
import { UserProvider } from './context/UserProvider.jsx';
createRoot(document.getElementById('root')).render(
    <BrowserRouter>
    <AuthProvider>
    <App />
    </AuthProvider>
    </BrowserRouter>
)
