import {useLocation} from "react-router";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import PlayNav from "./PlayNav";
import Lobby from "./Lobby";
import Game from "./Game";
import Websocket from "../../Websocket";

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
    const [roomStatus, setRoomStatus] = useState(null);
    const [gameState, setGameState] = useState(null);
    const [scoreboard, setScoreboard] = useState(null);
    const [games, setGames] = useState([]);

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

    function setFinished() {
        setRoomStatus(ROOM_STATUS.FINISHED);
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
                setScoreboard(room.scoreboard);
                setGameState(room.current_game);
                setRoomStatus(room.status);
                setGames(room.games);
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
                if (roomStatus !== ROOM_STATUS.IN_PROGRESS) {
                    setRoomStatus(ROOM_STATUS.IN_PROGRESS);
                }
            });
            stompClient.subscribe(ROOM_PREFIX + 'GAME_ADDED', (payload) => {
                const game = JSON.parse(payload.body);
                setGames(games => [...games, game]);
            });
            stompClient.subscribe(ROOM_PREFIX + 'GAME_REMOVED', (payload) => {
                const game = JSON.parse(payload.body);
                setGames(games => games.filter(g => g.id !== game.id));
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
                   roomCode={roomCode} games={games} timer={timer} isHost={isHost} myID={myID} startGame={startGame} stompClient={stompClient}>
            </Lobby>
            }{roomStatus === ROOM_STATUS.ABOUT_TO_START &&
            <div className={"d-flex flex-fill align-items-center"}>
                <div className={"container text-center"}>
                    <h1>Get ready!</h1>
                    <h2>The game is about to start...</h2>
                </div>
            </div>

            }{(roomStatus === ROOM_STATUS.IN_PROGRESS || roomStatus === ROOM_STATUS.FINISHED) &&
            // gameState ID is passed as key to force a re-render when the current game changes.
            <Game key={gameState.id} stompClient={stompClient} roomID={roomID} myID={myID}
                  setTimer={setTimer} scoreboard={scoreboard} setScoreboard={setScoreboard}
                  {...gameState} setFinished={setFinished} hostID={hostID}></Game>
            }
        </div>
    )
}

export default PlayPage;