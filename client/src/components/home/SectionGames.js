import img_perfect_clicker from "../../assets/img/games/perfect_clicker.png";
import img_traffic_light from "../../assets/img/games/traffic_light.png";
import img_placeholder from "../../assets/img/games/425x250.png";

function sectionGames() {
    return (
        <section id="games">
            <div class="container py-5">
                <div class="row mb-5">
                    <div class="col-md-8 col-xl-6 text-center mx-auto">
                        <h2 class="fw-bold">Games available</h2>
                    </div>
                </div>
                <div class="row row-cols-1 row-cols-md-2 mx-auto available-games-table">
                    <div class="col mb-4">
                        <div><img alt={""} class="rounded shadow w-100 fit-cover" height={250} data-bss-hover-animate="pulse" src={img_perfect_clicker} loading="lazy" />
                            <div class="py-4">
                                <h4 class="fw-bold">Perfect Clicker</h4>
                                <p class="text-muted">You have a target number. Click as fast as you can to reach it. But be careful - if you click over it, you lose!</p>
                            </div>
                        </div>
                    </div>
                    <div class="col mb-4">
                        <div><img alt={""} class="rounded shadow w-100 fit-cover" height={250} data-bss-hover-animate="pulse" src={img_traffic_light} loading="lazy" />
                            <div class="py-4">
                                <h4 class="fw-bold">Traffic light</h4>
                                <p class="text-muted">Pay attention to the colour on the screen. When it changes to your target colour, tap as fast as you can!</p>
                            </div>
                        </div>
                    </div>
                    <div class="col mb-4">
                        <div><img alt={""} class="rounded shadow w-100 fit-cover" height={250} data-bss-hover-animate="pulse" src={img_placeholder} loading="lazy" />
                            <div class="py-4">
                                <h4 class="fw-bold">Maths Mania</h4>
                                <p class="text-muted">Can you keep up with the equations? When you see a maths equation that is correct, tap the button as fast as you can!</p>
                            </div>
                        </div>
                    </div>
                    <div class="col mb-4">
                        <div><img alt={""} class="rounded shadow w-100 fit-cover" height={250} data-bss-hover-animate="pulse" src={img_placeholder} loading="lazy" />
                            <div class="py-4">
                                <h4 class="fw-bold">Colour Basher</h4>
                                <p class="text-muted">You are presented with a grid of colours. They change continuously. When you spot your colour, tap on it as many times as you can!</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    )
}

export default sectionGames;