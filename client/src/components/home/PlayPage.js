import Nav from "./Nav";
import {useLocation} from "react-router";
import {useNavigate} from "react-router-dom";
import {useState} from "react";

function PlayPage() {

    // Init
    const navigate = useNavigate();

    // State
    const [hostID, setHostID] = useState("");
    const [roomCode, setRoomCode] = useState("");

    // Redirect state
    const location = useLocation();
    const state = location.state;
    if (state === null) {
        return navigate("/");
    }
    const name = state.name;
    const session = state.session;
    const id = state.id;

    // Functions

    function isHost() {
        return hostID === id;
    }


    return (
        <>
            <Nav></Nav>
            <h1>Playing as {name} with session {session}</h1>
        </>
    )
}

export default PlayPage;