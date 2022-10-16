import PropTypes from "prop-types";

function Instructions(props) {
    return (
        <div className={"d-flex flex-fill align-items-center"}>
            <div className={"container text-center"}>
                <h1>{props.title}</h1>
                <div dangerouslySetInnerHTML={{ __html: props.instructions }}/>
            </div>
        </div>
    );
}


Instructions.propTypes = {
    instructions: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    type: PropTypes.string.isRequired,
}

export default Instructions;