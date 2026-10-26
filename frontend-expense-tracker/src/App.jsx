import { Route, Routes } from "react-router-dom"
import { ToastContainer } from "react-toastify"
import { Navbar } from "./components/Navbar"
import { HomePage } from "./pages/HomePage"
import LoginPage from "./pages/LoginPage"
import ProfilePage from "./pages/ProfilePage"
import RegisterPage from "./pages/RegisterPage"
function App() {

  return (
    <>
    
      <ToastContainer position="top-center" />
      <Navbar/>
      <Routes>
        <Route path="/" element={<HomePage/>}/>
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<LoginPage />} />
      </Routes>
    </>
  )
}
export default App