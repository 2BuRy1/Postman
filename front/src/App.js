import './styles/App.css';
import Login from "./Authorization/Login";
import Register from "./Authorization/Register";
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import Main from "./Authorization/Main";

function App() {
    return (
        <div className="App">
            <Router>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/" element={<Main/>} />
                </Routes>
            </Router>
        </div>
    );
}

export default App;