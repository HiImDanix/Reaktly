import Nav from "../home/Nav";
import {useLocation} from "react-router";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import * as SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import PlayNav from "./PlayNav";
import Lobby from "./Lobby";
import Countdown from "react-countdown";

function PlayPage() {

    // enum for room status
    const ROOM_STATUS = {
        LOBBY: "LOBBY",
        ABOUT_TO_START: "ABOUT_TO_START",
        IN_PROGRESS: "IN_PROGRESS",
        FINISHED: "FINISHED"
    }

    // Init
    const navigate = useNavigate();

    // State
    const [hostID, setHostID] = useState(null);
    const [roomCode, setRoomCode] = useState(null);
    const [roomID, setRoomID] = useState(null);
    const [players, setPlayers] = useState([]);
    const [timer, setTimer] = useState(null); // epoch to count down to
    const [stompClient, setStompClient] = useState(Stomp.over(() => new SockJS('http://localhost:8080/ws')));
    const [roomStatus, setRoomStatus] = useState(ROOM_STATUS.LOBBY);

    // Redirect state
    const location = useLocation();
    const state = location.state;
    const name = state?.name;
    const session = state?.session;
    const id = state?.id;

    // Functions

    function isHost() {
        return hostID === id;
    }

    useEffect(() => {
        // if state is null, redirect to homepage
        if (state === null) {
            return navigate("/");
        }

        stompClient.connect({Authorization:session}, onConnected, onError);

        function onError(error) {
            alert("Could not connect to the game servers!");
        }

        function onConnected() {
            setupLobby();
        }

        function setupLobby() {
            stompClient.subscribe("/user/queue/room", (payload) => {
                const room = JSON.parse(payload.body);
                console.log(room);
                setHostID(room.host.id);
                setRoomID(room.id);
                setPlayers(room.players);
                setRoomCode(room.code);
                setRoomStatus(room.status);
                if (room.status === ROOM_STATUS.ABOUT_TO_START) {
                    setTimer(room.start_time);
                }
                subscribe(room.id);
            });
            stompClient.send("/app/room");

        }

        function subscribe(roomID) {
            const ROOM_PREFIX = '/topic/room/' + roomID + '/';
            const GAMEPLAY_PREFIX = '/topic/room/' + roomID + '/gameplay/';
            stompClient.subscribe(ROOM_PREFIX + 'PLAYER_JOINED', (payload) => {
                const player = JSON.parse(payload.body);
                setPlayers(players => [...players, player]);
            });
            stompClient.subscribe(GAMEPLAY_PREFIX + 'PERFECT_CLICKER_CLICK', () => console.log("Click, Clack!"));
            stompClient.subscribe(GAMEPLAY_PREFIX + 'GAME_STARTED', (payload) => {
                const game = JSON.parse(payload.body);
                setTimer(game.start_time);
                setRoomStatus(ROOM_STATUS.ABOUT_TO_START);
            });
            stompClient.subscribe(GAMEPLAY_PREFIX + 'PERFECT_CLICKER_GAME_START', () => console.log("Perfect Clicker game has started"));
            stompClient.subscribe(GAMEPLAY_PREFIX + 'PERFECT_CLICKER_GAME_END', () => console.log("Perfect Clicker game has ended"));
            stompClient.subscribe(GAMEPLAY_PREFIX + 'END_GAME', () => console.log("Game end"));
        }


    }, []);


    function startGame() {
        stompClient.send("/app/gameplay.start");
    }

    return (
        <div className={"min-vh-100 d-flex flex-column"}>
            <PlayNav name={name} timer={timer}></PlayNav>
            {roomStatus === ROOM_STATUS.LOBBY &&
            <Lobby name={name} session={session} roomID={roomID} players={players}
                   roomCode={roomCode} timer={timer} isHost={isHost} startGame={startGame}>
            </Lobby>
            }{roomStatus === ROOM_STATUS.ABOUT_TO_START &&
            <div className={"d-flex flex-fill align-items-center"}>
                <div className={"container text-center"}>
                    <h1>Be prepared... The game is about to start!</h1>
                </div>
            </div>

            }{roomStatus === ROOM_STATUS.IN_PROGRESS &&
            <h1></h1>
            }
        </div>
    )
}

export default PlayPage;