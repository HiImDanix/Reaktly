import {BrowserRouter, Routes, Route} from "react-router-dom";

import MainPage from './components/home/MainPage';
import ContactPage from "./components/home/ContactPage";

function App() {
  return (
      <div className='App'>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<MainPage />} />
            <Route path="/contact" element={<ContactPage />} />
          </Routes>
        </BrowserRouter>

      </div>
    );
}

export default App;
