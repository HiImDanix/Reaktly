import {useState} from "react";
import { Link, useNavigate } from 'react-router-dom';

import { ACTION} from "./EnterNamePage";


function SectionPlay() {

    const navigate = useNavigate();

    const [roomCode, setRoomCode] = useState("");

    const validateRoomCode = async (e) => {
        e.preventDefault();
        const res = await fetch("http://192.168.0.210:8080/room_code/" + roomCode);
        console.log(res);
        if (res.status === 200) {
            navigate("/enter_name", { state: { roomCode: roomCode, action: ACTION.JOIN_ROOM } });
        } else {
            alert("Invalid room code");
        }
    }

    return (
    <header class="bg-dark" id="play">
        <div class="container pt-4 pt-xl-5">
            <div class="row d-xxl-flex pt-5">
                <div class="col-md-8 col-xl-6 text-center text-md-start mx-auto mb-4">
                    <div class="text-center">
                        <p class="fs-1 fw-bold text-success mb-3">Play reaction-based party games together, for free!</p>
                        <h1 class="fs-4">Gather your friends and start playing now! When you know the answer - click as fast as you can, and win!</h1>
                    </div>
                </div>
                <div class="col-12 col-lg-10 mx-auto" id="join-game">
                    <form class="d-flex justify-content-center flex-wrap" method="post">
                        <div class="mb-3">
                            <input class="form-control" type="text" required="" minLength="3"
                                   name="code" placeholder="Game Code" onChange={(e) => setRoomCode(e.target.value)}>

                            </input>
                        </div>
                        <div class="mb-3">
                            <button class="btn btn-primary ms-sm-2" onClick={validateRoomCode}>Join game</button>
                        </div>
                    </form>
                </div>
                <div class="col-12 col-lg-10 mx-auto" id="new-game">
                    <form class="d-flex justify-content-center flex-wrap">
                        <Link to="/enter_name" state={{ action: ACTION.NEW_ROOM }}>
                            <button class="btn btn-light">New game</button>
                        </Link>
                    </form>
                </div>
            </div>
        </div>
    </header>
    )
}

export default SectionPlay;