import {useLocation} from "react-router";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import PlayNav from "./PlayNav";
import Lobby from "./Lobby";
import Game from "./Game";
import Websocket from "../../Websocket";

import Config from "../../Config.json";

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
    const [stompClient, setStompClient] = useState(new Websocket());
    const [roomStatus, setRoomStatus] = useState(ROOM_STATUS.LOBBY);
    const [gameState, setGameState] = useState(null);

    // Redirect state
    const location = useLocation();
    const state = location.state;
    const name = state?.name;
    const session = state?.session;
    const myID = state?.id;

    // Functions

    function isHost() {
        return hostID === myID;
    }

    useEffect(() => {
        // if state is null, redirect to homepage
        if (state === null) {
            return navigate("/");
        }

        stompClient.connect(session, onConnected, onError);

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
                setGameState(room.current_game);
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
            stompClient.subscribe(ROOM_PREFIX + 'PREPARE_FOR_START', (payload) => {
                const preparation = JSON.parse(payload.body);
                setTimer(preparation.start_time);
                setRoomStatus(ROOM_STATUS.ABOUT_TO_START);
            });
            stompClient.subscribe(GAMEPLAY_PREFIX + 'GAME_START_INFO', (payload) => {
                const game = JSON.parse(payload.body);
                console.log(game);
                setGameState(game);
                setRoomStatus(ROOM_STATUS.IN_PROGRESS);
            });
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
            <Game stompClient={stompClient} roomID={roomID} myID={myID} setTimer={setTimer} {...gameState}></Game>
            }
        </div>
    )
}

export default PlayPage;