package com.imatia.campusdual.grupoun_bootcampbackend.model.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RoomTest {

    @Test
    public void allArgsConstructor_validArguments_RoomWithCorrectFieldsIsCreated() {
        int id = 1;
        Integer floorNumber = 79;
        Room room = new Room(id, floorNumber);

        assertEquals(id, room.getId());
        assertEquals(floorNumber, room.getRoomNumber());
    }



}
