package com.imatia.campusdual.grupoun_bootcampbackend.model.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name= "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer id;
    @Column(name="room_id")
    private Room room;
    @Column(name="check_in_date")

    private LocalDateTime checkInDate;
    @Column(name="check_out_date")

    private LocalDate checkOutDate;
    @Column

    private String DNI;
    @Column

    private String name;
    @Column

    private String surname1;
    @Column

    private String surname2;

    public Booking(Integer id, Room room, LocalDateTime checkInDate, LocalDate checkOutDate, String DNI, String name, String surname1, String surname2) {
        this.id = id;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.DNI = DNI;
        this.name = name;
        this.surname1 = surname1;
        this.surname2 = surname2;
    }

    public Booking(Integer id, Room room, LocalDateTime checkInDate, LocalDate checkOutDate, String DNI, String name, String surname1) {
        this.id = id;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.DNI = DNI;
        this.name = name;
        this.surname1 = surname1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname1() {
        return surname1;
    }

    public void setSurname1(String surname1) {
        this.surname1 = surname1;
    }

    public String getSurname2() {
        return surname2;
    }

    public void setSurname2(String surname2) {
        this.surname2 = surname2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) && Objects.equals(room, booking.room) && Objects.equals(checkInDate, booking.checkInDate) && Objects.equals(checkOutDate, booking.checkOutDate) && Objects.equals(DNI, booking.DNI) && Objects.equals(name, booking.name) && Objects.equals(surname1, booking.surname1) && Objects.equals(surname2, booking.surname2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, room, checkInDate, checkOutDate, DNI, name, surname1, surname2);
    }
}
