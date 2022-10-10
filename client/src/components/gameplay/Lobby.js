import PropTypes from "prop-types";

function Lobby(props) {
    return (
        <div className="bg-dark d-flex flex-column min-vh-100">
            <h1>Playing as {props.name} with session {props.session} and roomID: {props.roomID}</h1>
            <p>I am a host: {props.isHost() ? "Yes" : "No"}</p>
            <p>Room code: {props.roomCode}</p>
            <p>Players:</p>
            <ul>
                {props.players.map((player) => (
                    <li key={player.id}>{player.name}</li>
                ))}
            </ul>
            {props.isHost() &&
                <button className="btn btn-primary" onClick={props.startGame}>Start Game</button>
            }
        </div>
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
    isHost: PropTypes.func.isRequired,
    startGame: PropTypes.func.isRequired,
}

export default Lobby;