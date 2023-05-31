package com.ontimize.hr.model.core.util;

import org.springframework.stereotype.Component;

@Component
public class RoomUtils {

    public int getFloorNumber(int roomNumber) {
        int floorNumber = roomNumber;
        if (roomNumber < 1000) {
            while (floorNumber > 9) {
                floorNumber /= 10;
            }
        } else {
            while (floorNumber > 99) {
                floorNumber /= 10;
            }
        }
        return floorNumber;
    }

}
