package com.imatia.campusdual.grupoun_bootcampbackend.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "room_number")
    private Integer roomNumber;
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    public Room() {
    }

    public Room(Integer id, Integer roomNumber) {
        this.id = id;
        this.roomNumber = roomNumber;
    }

    public Room(Integer id, Integer roomNumber, Hotel hotel) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.hotel = hotel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
}
