function sectionContact() {
    return (
        <section class="py-5">
            <div class="container py-5">
                <div class="row">
                    <div class="col-md-8 col-xl-6 text-center mx-auto">
                        <p class="fs-2 fw-bold mb-2">Anything you'd like to tell me?</p>
                        <h2 class="fs-4 fw-normal text-success">Let's have a chat.</h2>
                    </div>
                </div>
                <div class="row d-flex justify-content-center">
                    <div class="col-md-6 col-xl-4">
                        <form class="p-3 p-xl-4" method="post">
                            <div class="mb-3"><input class="form-control" type="text" name="name" placeholder="Name" /></div>
                            <div class="mb-3"><input class="form-control" type="email"name="email" placeholder="Email" /></div>
                            <div class="mb-3"><textarea class="form-control" name="message" rows="6" placeholder="Message"></textarea></div>
                            <div><button class="btn btn-primary shadow d-block w-100" type="submit">Send</button></div>
                        </form>
                    </div>
                </div>
            </div>
        </section>
    )
}

export default sectionContact;
