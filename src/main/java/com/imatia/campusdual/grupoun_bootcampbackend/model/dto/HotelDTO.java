package com.imatia.campusdual.grupoun_bootcampbackend.model.dto;

import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HotelDTO {

    private int id;
    private String name;
    private int numberOfFloors;
    private List<Integer> roomIds;

    public HotelDTO() {
        roomIds = new ArrayList<>();
    }

    public HotelDTO(int id, String name, int numberOfFloors) {
        this.id = id;
        this.name = name;
        this.numberOfFloors = numberOfFloors;
        this.roomIds = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public void setNumberOfFloors(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
    }

    public List<Integer> getRoomIds() {
        return roomIds;
    }

    public void setIntegerIds(List<Integer> roomIds) {
        this.roomIds = roomIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelDTO hotelDTO = (HotelDTO) o;
        return id == hotelDTO.id && Objects.equals(name, hotelDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
