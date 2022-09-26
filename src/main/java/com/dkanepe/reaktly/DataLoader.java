package com.dkanepe.reaktly;

import com.dkanepe.reaktly.models.Room;
import com.dkanepe.reaktly.models.games.Game;
import com.dkanepe.reaktly.models.games.PerfectClicker.PerfectClicker;
import com.dkanepe.reaktly.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private final RoomRepository roomRepository;

    @Autowired
    public DataLoader(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public void run(ApplicationArguments args) {

        Room room1 = new Room();

        // todo: try saving with same name, make custom exception to catch
        try {
            roomRepository.save(room1);
            room1.setCode("password");
            Game game1 = new PerfectClicker(5);
            room1.getGames().add(game1);
            roomRepository.save(room1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}