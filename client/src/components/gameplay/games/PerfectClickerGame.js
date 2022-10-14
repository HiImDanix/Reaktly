import PropTypes from "prop-types";
import {useEffect, useState} from "react";

function PerfectClickerGame(props) {

    const [clicks, setClicks] = useState(() => {
            for (let gameState of props.state) {
                if (gameState.player.id === props.myID) {
                    return gameState.clicks;
                }
            }
            return 0;
    });
    const [target, setTarget] = useState(props.target)
    const [state, setState] = useState(props.state);

    useEffect(() => {
        const GAMEPLAY_PREFIX = '/topic/room/' + props.roomID + '/gameplay/';
        props.stompClient.subscribe(GAMEPLAY_PREFIX + 'PERFECT_CLICKER_CLICK', receiveClick);
    }, []);

    // click
    const handleClick = () => {
        props.stompClient.send("/app/gameplay/PerfectClicker.click");
    }

    const receiveClick = (payload) => {
        const data = JSON.parse(payload.body);
        if (props.myID === data.player_id) {
            console.log("Received click from myself");
            setClicks((clicks) => clicks + 1);
        }
    }

    return (
        <>
            <main className="d-flex flex-fill align-items-center" id="game-area">
                <div className="container">
                    <div className="d-flex flex-column justify-content-center align-items-center">
                        <button
                            className="btn btn-primary btn-lg border rounded-circle border-5 d-flex
                            justify-content-center align-items-center perfectClickerButton ratio ratio-1x1 w-75"
                            type="button" onClick={handleClick}>{clicks > 0 ? clicks : "Tap me"}
                        </button>
                    </div>
                </div>
            </main>
            <div className="text-center bg-dark-lighter mt-auto">
                <div className="container text-white py-3">
                    <h5 className="mb-0">Tap the button as many times as you can until the time runs out!</h5>
                </div>
            </div>
        </>
    )
}

PerfectClickerGame.propTypes = {
    stompClient: PropTypes.object.isRequired,
    roomID: PropTypes.number.isRequired,
    myID: PropTypes.number.isRequired,
    target_clicks: PropTypes.number.isRequired,
    state: PropTypes.array.isRequired,

}

export default PerfectClickerGame;