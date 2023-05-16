package com.imatia.campusdual.grupoun_bootcampbackend.model.dto;

import java.util.Objects;

public class HotelDTO {

    private int id;
    private String name;

    public HotelDTO() {
    }

    public HotelDTO(int id, String name) {
        this.id = id;
        this.name = name;
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
