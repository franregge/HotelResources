package com.imatia.campusdual.grupoun_bootcampbackend.model.dto;

import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;

import java.util.Objects;

public class RoomDTO {

    private int id;
    private int roomNumber;
    private int hotelId;

    public RoomDTO() {

    }

    public RoomDTO(int id, int roomNumber) {
        this.id = id;
        this.roomNumber = roomNumber;
    }

    public RoomDTO(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public int getFloorNumber() {
        int floorNumber = roomNumber;

        while (floorNumber > 9) {
            floorNumber /= 10;
        }

        return floorNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomDTO roomDTO = (RoomDTO) o;
        return id == roomDTO.id && roomNumber == roomDTO.roomNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomNumber);
    }
}
