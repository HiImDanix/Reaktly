import {ReactComponent as Logo} from "../../assets/img/logo_inverse.svg";
import Countdown from "react-countdown";

function PlayNav(props) {

    // timer renderer
    const renderer = ({ hours, minutes, seconds, completed }) => {
        return (
            <span>
                {minutes.toString().padStart(2, '0')}:
                {seconds.toString().padStart(2, '0')}
            </span>
        );
    };

    console.log(props.timer + " and now: " + Date.now());
    return (
        <nav className="navbar navbar-dark sticky-top" id="gameNav">
            <div className="container">
                <div className="container justify-content-center">
                    <div className="row d-flex flex-row justify-content-center">
                        <div className="col d-flex align-items-center">
                            <a className="navbar-brand d-flex flex-row flex-fill">
                                <span className="bs-icon-sm me-2 bs-icon">
                                    <Logo></Logo>
                                </span>
                                <span className="fw-bold">Reaktly</span>
                            </a>
                        </div>
                        <div className="col d-flex justify-content-center align-items-center">
                            {props.timer &&
                                <div className="text-bg-primary border rounded border-0 py-1 px-3">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor"
                                         viewBox="0 0 16 16" className="bi bi-clock d-none d-sm-inline-block">
                                        <path
                                            d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 0 .252.434l3.5 2a.5.5 0 0 0 .496-.868L8 8.71V3.5z"></path>
                                        <path
                                            d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm7-8A7 7 0 1 1 1 8a7 7 0 0 1 14 0z"></path>
                                    </svg>
                                    <span className="border-0 ps-sm-2">
                                    <Countdown date={props.timer} renderer={renderer}/>
                                </span>
                                </div>
                            }

                        </div>
                        <div className="col d-flex justify-content-end align-items-center">
                            <span className="text-end d-md-flex flex-fill justify-content-md-end align-items-md-center">
                                <a className="d-flex d-md-flex" href="#">Log out</a>
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </nav>
    )
}

export default PlayNav;