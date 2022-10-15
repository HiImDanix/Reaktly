package com.dkanepe.reaktly;

import com.dkanepe.reaktly.models.Player;
import com.dkanepe.reaktly.models.Room;
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

        Room room = new Room(new Player("Dennis"));
        room.getGames().add(new PerfectClicker());
        roomRepository.save(room);
        room.setCode("1234");
        roomRepository.save(room);

    }
}