import {ReactComponent as Logo} from "../../assets/img/logo_inverse.svg";
import { HashLink as Link } from 'react-router-hash-link';

function Nav() {
  return (
    <nav class="navbar navbar-dark navbar-expand-md sticky-top navbar-shrink py-3" id="mainNav">
      <div class="container">
        <Link to ="/" class="navbar-brand d-flex align-items-center">
            <span className="bs-icon-sm me-2 bs-icon">
                <Logo></Logo>
            </span>
            <span>Reaktly</span>
        </Link>
        <button data-bs-toggle="collapse" class="navbar-toggler" data-bs-target="#navcol-1">
          <span class="visually-hidden">Toggle navigation</span><span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navcol-1">
          <ul class="navbar-nav ms-auto">
            <li class="nav-item"><Link to ="/#play" class="nav-link">Play</Link></li>
            <li class="nav-item"><Link to ="/#games" class="nav-link">Games</Link></li>
            <li class="nav-item"><Link to ="/contact" class="nav-link">Feedback</Link></li>
          </ul>
        </div>
      </div>
    </nav>
  );
}

export default Nav;