import '../../assets/bootstrap/css/bootstrap.min.css';
import '../../assets/scss/main.scss';



import SectionGames from "./SectionGames";
import Footer from "./Footer";
import SectionPlay from "./SectionPlay";
import Nav from "./Nav";
import {useEffect} from "react";
import {useNavigate} from "react-router-dom";


function MainPage(){

    const navigate = useNavigate();

    useEffect(() => {
        // if session is valid, redirect to play page
        const session = localStorage.getItem("session");
        const id = localStorage.getItem("id");
        const name = localStorage.getItem("name");
        if (session !== null && id !== null && name !== null) {
            console.log("Redirecting to play page");
            // make a request to check if session is valid
            fetch("http://localhost:8080/player/session/" + session)
                .then(res => {
                    if (res.status === 200) {
                        return navigate("/play", {state: {name: name, session: session, id: id}});
                    } else {
                        localStorage.removeItem("session");
                    }
                }
            )

        }
    }, []);

    return (
        <>
            <Nav></Nav>
            <SectionPlay></SectionPlay>
            <SectionGames></SectionGames>
            <Footer></Footer>
        </>
    )
}

export default MainPage;