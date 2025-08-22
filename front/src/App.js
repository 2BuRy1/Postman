import './styles/App.css';
import Login from "./Authorization/Login";
import Register from "./Authorization/Register";
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import Main from "./MainPage/Main";
import SubscriptionComponent from "./MainPage/SubscriptionComponent";

function App() {
    return (
        <div className="App">
            <Router>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/" element={<Main/>} />
                    <Route path="/notification/:id/subscribe" element={<SubscriptionComponent />} />
                </Routes>
            </Router>
        </div>
    );
}

export default App;