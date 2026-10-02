import { ToastContainer } from "react-toastify"
import LoginPage from "./pages/LoginPage"
import RegisterPage from "./pages/RegisterPage"
import { BrowserRouter, Routes ,Route} from "react-router-dom"
function App() {

  return (
    <BrowserRouter>
      <ToastContainer position="top-center"/>
      <Routes>
        <Route path="/" element={<RegisterPage/>} />
        <Route path="/login" element={<LoginPage/>} />
      </Routes>
    </BrowserRouter>
  )
}
export default App