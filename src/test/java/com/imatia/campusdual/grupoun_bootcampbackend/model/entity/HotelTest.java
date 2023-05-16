package com.imatia.campusdual.grupoun_bootcampbackend.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HotelTest {

    @Test
    public void allArgsConstructor_validArguments_HotelWithCorrectFieldsIsCreated() {
        int id = 1;
        String name = "Overlook hotel";
        Hotel hotel = new Hotel(id, name);

        assertEquals(id, hotel.getId());
        assertEquals(name, hotel.getName());
    }

}
