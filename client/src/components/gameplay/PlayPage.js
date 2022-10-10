import Nav from "../home/Nav";
import {useLocation} from "react-router";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import * as SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

function PlayPage() {

    // Init
    const navigate = useNavigate();
    let stompClient;

    // State
    const [hostID, setHostID] = useState(null);
    const [roomCode, setRoomCode] = useState(null);
    const [roomID, setRoomID] = useState(null);
    const [players, setPlayers] = useState([]);

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

        // Connect to websockets using factory
        // var socket = ;
        stompClient = Stomp.over(() => new SockJS('http://localhost:8080/ws'));
        stompClient.connect({Authorization:session}, onConnected, onError);

        function onError(error) {
            alert("Could not connect to the game servers!");
        }

        function onConnected() {
            setupLobby();
            subscribe();
        }

        function setupLobby() {
            stompClient.subscribe("/user/queue/room", (payload) => {
                const room = JSON.parse(payload.body);
                console.log(room);
                setHostID(room.host.id)
                setRoomID(room.id)
                setPlayers(room.players)
                setRoomCode(room.code)
            });
            stompClient.send("/app/room");
        }

        function subscribe() {
            const ROOM_PREFIX = '/topic/room/' + roomID + '/';
            const GAMEPLAY_PREFIX = '/topic/room/' + roomID + '/gameplay/';
            stompClient.subscribe(ROOM_PREFIX + 'PLAYER_JOINED', (payload) => {
                const player = JSON.parse(payload.body);
                setPlayers(players => [...players, player]);
            });
            stompClient.subscribe(GAMEPLAY_PREFIX + 'PERFECT_CLICKER_CLICK', () => console.log("Click, Clack!"));
            stompClient.subscribe(GAMEPLAY_PREFIX + 'GAME_STARTED', () => console.log("The game is about to start"));
            stompClient.subscribe(GAMEPLAY_PREFIX + 'PERFECT_CLICKER_GAME_START', () => console.log("Perfect Clicker game has started"));
            stompClient.subscribe(GAMEPLAY_PREFIX + 'PERFECT_CLICKER_GAME_END', () => console.log("Perfect Clicker game has ended"));
            stompClient.subscribe(GAMEPLAY_PREFIX + 'END_GAME', () => console.log("Game end"));
        }


    }, []);


    return (
        <>
            <Nav></Nav>
            <h1>Playing as {name} with session {session} and roomID: {roomID}</h1>
            <p>I am a host: {isHost() ? "Yes" : "No"}</p>
            <p>Room code: {roomCode}</p>
            <p>Players:</p>
            <ul>
                {players.map((player) => (
                    <li key={player.id}>{player.name}</li>
                ))}
            </ul>
        </>
    )
}

export default PlayPage;