package com.imatia.campusdual.grupoun_bootcampbackend.model.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "room_id")
    @OneToOne
    private Room room;
    @Column(name = "check_in_date")
    private LocalDateTime checkInDate;
    @Column(name = "check_out_date")
    private LocalDate checkOutDate;
    @Column(name = "dni")
    private String clientDNI;
    @Column(name = "name")
    private String clientName;
    @Column(name = "surname1")
    private String clientSurname1;
    @Column(name = "surname2")
    private String clientSurname2;

    public Booking(Integer id, Room room, LocalDateTime checkInDate, LocalDate checkOutDate, String DNI, String name, String surname1, String surname2) {
        this.id = id;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.clientDNI = DNI;
        this.clientName = name;
        this.clientSurname1 = surname1;
        this.clientSurname2 = surname2;
    }

    public Booking(Integer id, Room room, LocalDateTime checkInDate, LocalDate checkOutDate, String DNI, String name, String surname1) {
        this.id = id;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.clientDNI = DNI;
        this.clientName = name;
        this.clientSurname1 = surname1;
    }

    public Booking() {
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
        return clientDNI;
    }

    public String getClientDNI() {
        return clientDNI;
    }

    public void setClientDNI(String clientDNI) {
        this.clientDNI = clientDNI;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientSurname1() {
        return clientSurname1;
    }

    public void setClientSurname1(String clientSurname1) {
        this.clientSurname1 = clientSurname1;
    }

    public String getClientSurname2() {
        return clientSurname2;
    }

    public void setClientSurname2(String clientSurname2) {
        this.clientSurname2 = clientSurname2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) && Objects.equals(room, booking.room) && Objects.equals(checkInDate, booking.checkInDate) && Objects.equals(checkOutDate, booking.checkOutDate) && Objects.equals(clientDNI, booking.clientDNI) && Objects.equals(clientName, booking.clientName) && Objects.equals(clientSurname1, booking.clientSurname1) && Objects.equals(clientSurname2, booking.clientSurname2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, room, checkInDate, checkOutDate, clientDNI, clientName, clientSurname1, clientSurname2);
    }
}
