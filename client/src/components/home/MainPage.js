import '../../assets/bootstrap/css/bootstrap.min.css';
import '../../assets/scss/main.scss';



import SectionGames from "./SectionGames";
import Footer from "./Footer";
import SectionPlay from "./SectionPlay";
import Nav from "./Nav";


function MainPage(){

    return (
        <>
            <Nav></Nav>
            <SectionPlay></SectionPlay>
            <SectionGames></SectionGames>
            <Footer></Footer>
        </>
    );
}

export default MainPage;