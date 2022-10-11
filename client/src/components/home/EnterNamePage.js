import Nav from "./Nav";
import { useLocation } from "react-router";
import {useNavigate} from 'react-router-dom';
import {useState, useEffect} from "react";

const ACTION = {
    JOIN_ROOM: 0,
    NEW_ROOM: 1,
}

function EnterNamePage() {

    // Init
    const navigate = useNavigate();

    // State values
    const [name, setName] = useState("");

    // Redirect state
    const location = useLocation();
    const state = location.state;
    const action = state?.action;
    const roomCode = state?.roomCode;

    // Functions

    useEffect(() => {
        if (state === null) {
            return navigate("/");
        }
    }, [state, navigate]);

    async function createGame(e) {
        e.preventDefault();
        const res = await fetch("http://192.168.0.210:8080/player?name=" + name, {
            method: "POST",
        });
        const data = await res.json();

        if (res.status === 200) {
            // put state in local storage
            localStorage.setItem("session", data.session);
            localStorage.setItem("id", data.id);
            localStorage.setItem("name", data.name);
            // redirect to play page
            navigate("/play", {state: {name: data.name, session: data.session, id: data.id}});
        } else {
            alert("Error creating game");
        }
    }

    async function joinGame(e) {
        e.preventDefault();
        const res = await fetch("http://192.168.0.210:8080/join", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                name: name,
                room_code: roomCode,
            }),
        });
        const data = await res.json();

        if (res.status === 200) {
            // put state in local storage
            localStorage.setItem("session", data.session);
            localStorage.setItem("id", data.id);
            localStorage.setItem("name", data.name);
            navigate("/play", {state: {name: data.name, session: data.session, id: data.id}});
        } else {
            alert("Error joining game");
        }
    }

    switch (action) {
        case ACTION.JOIN_ROOM:
            return (
                <>
                    <Nav></Nav>
                    <div className="container name-container" id="name-container">
                        <div className="row">
                            <div className="col-lg-10 offset-lg-1 text-center">
                                <h1 className="mb-3">What's your name?</h1>
                                <form>
                                    <input className="form-control mb-3" type="text" onChange={e => {setName(e.target.value)}} />
                                    <button className="btn btn-primary" type="submit" onClick={joinGame}>Join Game</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </>
            )
        case ACTION.NEW_ROOM:
            return (
                <>
                    <Nav></Nav>
                    <div className="container name-container" id="name-container">
                        <div className="row">
                            <div className="col-lg-10 offset-lg-1 text-center">
                                <h1 className="mb-3">What's your name?</h1>
                                <form>
                                    <input className="form-control mb-3" type="text" onChange={e => {setName(e.target.value)}} />
                                    <button className="btn btn-primary" type="submit" onClick={createGame}>Create game</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </>
            )
        default:
            console.log("Invalid action");
            navigate("/");

    }
}

export default EnterNamePage;
export {ACTION};