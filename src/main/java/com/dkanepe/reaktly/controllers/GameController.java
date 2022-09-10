package com.dkanepe.reaktly.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @GetMapping(value="/game/{id}")
    public String getGame(@PathVariable("id") Long id) {
        return "Game " + id;
    }
}
