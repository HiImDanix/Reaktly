import PropTypes from "prop-types";
import {useEffect, useState} from "react";
import Instructions from "./Instructions";
import GameType from "./GameType";
import PerfectClickerGame from "./games/PerfectClickerGame";
import Scoreboard from "./Scoreboard";

function Game(props) {

    const GAME_STATUS = {
        INSTRUCTIONS: "INSTRUCTIONS",
        IN_PROGRESS: "IN_PROGRESS",
        FINISHED: "FINISHED"
    }

    const [startTime, setStartTime] = useState(props.start_time);
    const [endTime, setEndTime] = useState(props.end_time);
    const [finish_time, setFinishTime] = useState(props.finish_time);
    const [id, setId] = useState(props.id);
    const [instructions, setInstructions] = useState(props.instructions);
    const [shortInstructions, setShortInstructions] = useState(props.short_instructions);
    const [status, setStatus] = useState(props.status);
    const [title, setTitle] = useState(props.title);
    const [type, setType] = useState(props.type);
    const [game, setGame] = useState(props.game);
    const [gameStats, setGameStats] = useState(props.statistics);


    useEffect(() => {
        const GAMEPLAY_PREFIX = '/topic/room/' + props.roomID + '/gameplay/';
        if (status === GAME_STATUS.INSTRUCTIONS) {
            props.setTimer(startTime);
        } else if (status === GAME_STATUS.IN_PROGRESS) {
            props.setTimer(endTime);
        } else if (status === GAME_STATUS.FINISHED) {
            props.setTimer(finish_time);
        }

        props.stompClient.subscribe(GAMEPLAY_PREFIX + 'GAME_START_PING', (payload) => {
            props.setTimer(endTime);
            setStatus(GAME_STATUS.IN_PROGRESS);
        });
        props.stompClient.subscribe(GAMEPLAY_PREFIX + 'GAME_END', (payload) => {
            const gameEnd = JSON.parse(payload.body);
            console.log(gameEnd);
            props.setScoreboard(gameEnd.scoreboard);
            setGameStats(gameEnd.statistics);
            setStatus(GAME_STATUS.FINISHED);
            if (gameEnd.is_last_game) {
                props.setFinished();
            } else {
                props.setTimer(finish_time);
            }
        });

        // subscribe to game specific events
        if (type === GameType.PERFECT_CLICKER) {
            props.stompClient.subscribe(GAMEPLAY_PREFIX + 'PERFECT_CLICKER_CLICKS');
        }
    }, []);

    switch (status) {
        case GAME_STATUS.INSTRUCTIONS:
            return (<Instructions title={title} instructions={instructions} type={type}></Instructions>);
        case GAME_STATUS.IN_PROGRESS:
            switch (type) {
                case GameType.PERFECT_CLICKER:
                    return (<PerfectClickerGame myID={props.myID} roomID={props.roomID} stompClient={props.stompClient}
                                                shortInstructions={shortInstructions} {...game}></PerfectClickerGame>);
                    case GameType.TRAFFIC_LIGHT:
                        return (<div>Traffic Light</div>);
                default:
                    return (<div>Error: Unknown game type</div>);
            }
        case GAME_STATUS.FINISHED:
            return (<Scoreboard scoreboard={props.scoreboard} gameStats={gameStats}></Scoreboard>);
        default:
            return (<>Error: game status not recognised</>);
    }
}

Game.propTypes = {
    end_time: PropTypes.number.isRequired,
    finish_time: PropTypes.number.isRequired,
    game: PropTypes.object.isRequired,
    id: PropTypes.number.isRequired,
    instructions: PropTypes.string.isRequired,
    short_instructions: PropTypes.string.isRequired,
    start_time: PropTypes.number.isRequired,
    status: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    type: PropTypes.string.isRequired,
    setTimer: PropTypes.func.isRequired,
    stompClient: PropTypes.object.isRequired,
    roomID: PropTypes.number.isRequired,
    myID: PropTypes.number.isRequired,
    scoreboard: PropTypes.object.isRequired,
    setScoreboard: PropTypes.func.isRequired,
}

export default Game;
