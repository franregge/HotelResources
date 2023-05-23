package com.imatia.campusdual.grupoun_bootcampbackend.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HotelTest {

    @Test
    public void allArgsConstructor_validArguments_HotelWithCorrectFieldsIsCreated() {
        int id = 1;
        String name = "Overlook hotel";
        Integer numberOfFloors = 5;
        Hotel hotel = new Hotel(id, name, numberOfFloors);

        assertEquals(id, hotel.getId());
        assertEquals(name, hotel.getName());
        assertEquals(numberOfFloors, hotel.getNumberOfFloors());
    }


}
