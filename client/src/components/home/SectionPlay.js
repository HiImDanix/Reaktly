function SectionPlay() {
    return (
    <header class="bg-dark" id="play">
        <div class="container pt-4 pt-xl-5">
            <div class="row d-xxl-flex pt-5">
                <div class="col-md-8 col-xl-6 text-center text-md-start mx-auto mb-4">
                    <div class="text-center">
                        <p class="fs-1 fw-bold text-success mb-3">Play reaction-based party games together, for free!</p>
                        <h1 class="fs-4">Gather your friends and start playing now! When you know the answer - click as fast as you can, and win!</h1>
                    </div>
                </div>
                <div class="col-12 col-lg-10 mx-auto" id="join-game">
                    <form class="d-flex justify-content-center flex-wrap" method="post">
                        <div class="mb-3"><input class="form-control" type="text" required="" minlength="3" name="code" placeholder="Game Code" /></div>
                        <div class="mb-3"><button class="btn btn-primary ms-sm-2" type="submit">Join game</button></div>
                    </form>
                </div>
                <div class="col-12 col-lg-10 mx-auto" id="new-game">
                    <form class="d-flex justify-content-center flex-wrap" method="post"><button class="btn btn-light" type="submit">New game</button></form>
                </div>
            </div>
        </div>
    </header>
    )
}

export default SectionPlay;