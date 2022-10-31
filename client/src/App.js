import {BrowserRouter, Routes, Route} from "react-router-dom";

import MainPage from './components/home/pages/MainPage';
import ContactPage from "./components/home/pages/ContactPage";
import PlayPage from "./components/gameplay/pages/PlayPage";
import EnterNamePage from "./components/home/pages/EnterNamePage";

function App() {
  return (
      <div className='App'>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<MainPage />} />
            <Route path="/enter_name" element={<EnterNamePage />} />
            <Route path="/play" element={<PlayPage />} />
            <Route path="/contact" element={<ContactPage />} />
          </Routes>
        </BrowserRouter>

      </div>
    );
}

export default App;
