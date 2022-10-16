import PropTypes from "prop-types";

function Scoreboard(props) {
    return (
        <div className={"d-flex flex-fill align-items-center"}>
            <div class="container">
                <div class="row">
                    <div class="col-md-6">
                        <h3>Statistics</h3>
                        <div class="table-responsive">
                            <table class="table">
                                <thead>
                                <tr>
                                    {props.gameStats.headers.map((header, index) => {
                                        return <th key={"stats-header-" + index}>{header}</th>
                                    })}
                                </tr>
                                </thead>
                                <tbody>
                                    {props.gameStats.rows.map((row, index) => {
                                        return (
                                            <tr key={"stats-row-" + index}>
                                            {row.map((cell, index2) => {
                                                return <td key={"stats-cell-" + index + "-" + index2}>{cell}</td>
                                            })}
                                            </tr>
                                        )
                                    })}
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <h3>Scoreboard</h3>
                        <div class="table-responsive">
                            <table class="table">
                                <thead>
                                <tr>
                                    {props.scoreboard.headers.map((header) => {
                                        return <th key={"scoreboard-header-" + header}>{header}</th>
                                    })}
                                </tr>
                                </thead>
                                <tbody>
                                    {props.scoreboard.rows.map((row, index) => {
                                        return (
                                            <tr key={"scoreboard-row-" + index}>
                                            {row.map((cell, index2) => {
                                                return <td key={"scoreboard-cell-" + index + "-" + index2}>{cell}</td>
                                            })}
                                            </tr>
                                        )
                                    })}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

Scoreboard.propTypes = {
    gameStats: PropTypes.object.isRequired,
    scoreboard: PropTypes.object.isRequired,
}

export default Scoreboard;