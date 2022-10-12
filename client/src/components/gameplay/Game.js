import PropTypes from "prop-types";
import {useEffect, useState} from "react";
import Instructions from "./Instructions";

function Game(props) {

    const GAME_STATUS = {
        INSTRUCTIONS: "INSTRUCTIONS",
        IN_PROGRESS: "IN_PROGRESS",
        SCOREBOARD: "SCOREBOARD",
        FINISHED: "FINISHED"
    }

    const stomClient = props.stompClient;
    const roomID = props.roomID;

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

    useEffect(() => {
        props.setTimer(startTime);
    }, []);

    switch (status) {
        case GAME_STATUS.INSTRUCTIONS:
            return (<Instructions title={title} instructions={instructions} type={type}></Instructions>);
        case GAME_STATUS.IN_PROGRESS:
            return (<>Game in progress</>);
        case GAME_STATUS.SCOREBOARD:
            return (<>Scoreboard</>);
        case GAME_STATUS.FINISHED:
            return (<>Game finished</>);
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
}

export default Game;
