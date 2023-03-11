import PropTypes from "prop-types";
import QRCode from "react-qr-code";
import {useState} from "react";

import img_perfect_clicker from "../../assets/img/games/perfect_clicker.png";
import img_traffic_light from "../../assets/img/games/traffic_light.png";

import Config from "../../Config.js";

function Lobby(props) {

    const perfectClicker = {
        type: "PERFECT_CLICKER",
        title: "Perfect Clicker",
        instructions: "You have a target number. Click as fast as you can to reach it. But be careful - if you click over the target, you lose!",
        image: img_perfect_clicker
    }

    const trafficLight = {
        type: "TRAFFIC_LIGHT",
        title: "Traffic Light",
        instructions: "Pay attention to the colour on the screen. When it changes to your target colour - tap as fast as you can!",
        image: img_traffic_light
    }

    // get games count by type
    const getGameCount = (myGame) => {
        let count = 0;
        props.games.forEach(game => {
            if (game.type === myGame.type) {
                count++;
            }
        });
        return count;
    }

    // increase/decrease game count
    const updateGameCount = (game, increase) => {
        if (increase) {
            props.stompClient.send("/app/room.add_game", JSON.stringify({
                "type": game.type,
            }));
        } else {
            const gameToRemove = props.games.find(g => g.type === game.type);
            if (gameToRemove) {
                props.stompClient.send("/app/room.remove_game", JSON.stringify({
                    "id": gameToRemove.id
                }));
            }
        }
    }

    const getJoinRoomURL = () => {
        const url = new URL(Config.CLIENT_URL);
        url.searchParams.append("room_code", props.roomCode);
        return url.toString();
    }

    return (
        <>
            <div className="container py-3 py-xl-4">
                <div className="row">
                    <div className="col-6 text-end">
                        <QRCode value={getJoinRoomURL()}
                                size={150} bgColor={"#fff"} fgColor={"#2d2c38"} style={{outline: "10px solid white"}}/>
                    </div>

                    <div className="col-6 col-md-3 text-start d-flex flex-column justify-content-center align-items-start">
                        <h5 className="fw-bold"><span>{props.roomCode}</span><br /></h5>
                        <p className="w-100 text-break">Invite friends! Share room code above, or <a href={getJoinRoomURL()}>{getJoinRoomURL()}</a>
                        </p>
                    </div>
                </div>
            </div>

            {props.isHost() && (

            <div className="container pt-3 pt-md-4 pt-lg-5">

                <div className="row mb-3">
                    <div className="col text-center">
                        <h2>Games</h2>
                        <p>Select the games that you would like to play</p>
                    </div>
                </div>

                <div className="row gy-4 row-cols-2 row-cols-md-3 row-cols-xl-4 row-cols-xxl-5 justify-content-center">
                    <div className="col">
                        <div className="position-relative">
                            <span className="badge bg-primary fs-6 position-absolute top-0 start-50 translate-middle bg-primary">{getGameCount(perfectClicker)}</span>
                            <img alt={perfectClicker.title} className="rounded d-block w-100 fit-cover" src={img_perfect_clicker} />
                            <div className="btn-group btn-group-sm w-100 mt-2" role="group">
                                <button className={"btn btn-danger" + (getGameCount(perfectClicker) === 0 ? " disabled" : "")} onClick={() => updateGameCount(perfectClicker, false)}>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" viewBox="0 0 16 16" className="bi bi-dash-lg">
                                        <path fillRule="evenodd" d="M2 8a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11A.5.5 0 0 1 2 8Z"></path>
                                    </svg>
                                </button>
                                <button className="btn btn-success" type="button" onClick={() => updateGameCount(perfectClicker, true)}>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" viewBox="0 0 16 16" className="bi bi-dash-lg">
                                        <path fillRule="evenodd" d="M8 2a.5.5 0 0 1 .5.5v5h5a.5.5 0 0 1 0 1h-5v5a.5.5 0 0 1-1 0v-5h-5a.5.5 0 0 1 0-1h5v-5A.5.5 0 0 1 8 2Z"></path>
                                    </svg>
                                </button>
                            </div>
                            <div className="py-4">
                                <h4>{perfectClicker.title}</h4>
                                <p>{perfectClicker.instructions}</p>
                            </div>
                        </div>
                    </div>

                    <div className="col">
                        <div className="position-relative">
                            <span className="badge bg-primary fs-6 position-absolute top-0 start-50 translate-middle bg-primary">{getGameCount(trafficLight)}</span>
                            <img alt={trafficLight.title} className="rounded d-block w-100 fit-cover" src={img_traffic_light} />
                            <div className="btn-group btn-group-sm w-100 mt-2" role="group">
                                <button className={"btn btn-danger" + (getGameCount(trafficLight) === 0 ? " disabled" : "")} type="button" onClick={() => updateGameCount(trafficLight, false)}>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" viewBox="0 0 16 16" className="bi bi-dash-lg">
                                        <path fillRule="evenodd" d="M2 8a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-11A.5.5 0 0 1 2 8Z"></path>
                                    </svg>
                                </button>
                                <button className="btn btn-success" type="button" onClick={() => updateGameCount(trafficLight, true)}>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" viewBox="0 0 16 16" className="bi bi-dash-lg">
                                        <path fillRule="evenodd" d="M8 2a.5.5 0 0 1 .5.5v5h5a.5.5 0 0 1 0 1h-5v5a.5.5 0 0 1-1 0v-5h-5a.5.5 0 0 1 0-1h5v-5A.5.5 0 0 1 8 2Z"></path>
                                    </svg>
                                </button>
                            </div>
                            <div className="py-4">
                                <h4>{trafficLight.title}</h4>
                                <p>{trafficLight.instructions}</p>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
            )}



            <div className="container pt-3 pt-xl-5 pb-5">

                <div className="row mb-3">
                    <div className="col text-center">
                        <h2>Players</h2>
                    </div>
                </div>

                <div className="row gy-4 row-cols-3 row-cols-md-4 row-cols-xl-5 pb-5">
                    {props.players.map((player) => (
                        <div className="col" key={player.id}>
                            <h5 className="fw-semibold text-center mb-0 text-break text-wrap">{player.name} {player.id === props.myID ? "(you)" : ""}</h5>
                        </div>
                    ))}
                </div>
            </div>

            {props.isHost() && (
                <div className={"container"}>
                    <button className="btn btn-primary mb-3 w-100" type="button" onClick={props.games.length > 0 ? props.startGame : () => alert("Please select at least one game")}>Start Game</button>
                </div>
            )}

        </>
    )
}


// props
Lobby.propTypes = {
    name: PropTypes.string.isRequired,
    session: PropTypes.string.isRequired,
    roomID: PropTypes.number,
    players: PropTypes.array.isRequired,
    roomCode: PropTypes.string,
    timer: PropTypes.number,
    myID: PropTypes.string,
    isHost: PropTypes.func.isRequired,
    startGame: PropTypes.func.isRequired,
    stompClient: PropTypes.object.isRequired,
    games: PropTypes.array.isRequired,
}

export default Lobby;