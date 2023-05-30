package com.ontimize.hr.model.core.util;

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
