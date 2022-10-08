import {ReactComponent as Logo} from "../../assets/img/logo_inverse.svg";
import {Link} from "react-router-dom";

function Nav() {
  return (
    <nav class="navbar navbar-dark navbar-expand-md sticky-top navbar-shrink py-3" id="mainNav">
      <div class="container">
        <a class="navbar-brand d-flex align-items-center" href="/">
                    <span class="bs-icon-sm me-2 bs-icon">
                        <Logo></Logo>
                    </span>
          <span>Reaktly</span>
        </a>
        <button data-bs-toggle="collapse" class="navbar-toggler" data-bs-target="#navcol-1">
          <span class="visually-hidden">Toggle navigation</span><span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navcol-1">
          <ul class="navbar-nav ms-auto">
            <li class="nav-item"><a class="nav-link active" href="index.html#play">Play</a></li>
            <li class="nav-item"><a class="nav-link active" href="#games">Games</a></li>
            <li class="nav-item"></li>
            <li class="nav-item"></li>
            <li class="nav-item"></li>
            <li class="nav-item"><Link to ="/contact" class="nav-link">Feedback</Link></li>
          </ul>
        </div>
      </div>
    </nav>
  );
}

export default Nav;