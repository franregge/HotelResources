package com.imatia.campusdual.grupoun_bootcampbackend.util;

import org.springframework.stereotype.Component;

@Component
public class RoomUtils {

    public int getFloorNumber(int roomNumber) {
        int floorNumber = roomNumber;

        while (floorNumber > 9) {
            floorNumber /= 10;
        }

        return floorNumber;
    }

}
