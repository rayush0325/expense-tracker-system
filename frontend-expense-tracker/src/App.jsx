import { Route, Routes } from "react-router-dom"
import { ToastContainer } from "react-toastify"
import { Navbar } from "./components/Navbar"
import { HomePage } from "./pages/HomePage"
import LoginPage from "./pages/LoginPage/LoginPage"
import ProfilePage from "./pages/ProfilePage"
import RegisterPage from "./pages/RegisterPage/RegisterPage"
function App() {

  return (
    <>
    
      <ToastContainer position="top-center" />
      <Navbar/>
      <Routes>
        <Route path="/" element={<HomePage/>}/>
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/profile" element={<ProfilePage />} />
      </Routes>
    </>
  )
}
export default App