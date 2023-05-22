package com.imatia.campusdual.grupoun_bootcampbackend.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class BookingDTO {
    private int id;
    private int roomId;
    private LocalDateTime checkInDate;
    private LocalDate checkOutDate;
    private String clientDNI;
    private String clientName;
    private String clientSurname1;
    private String clientSurname2;

    public BookingDTO(Integer id, int roomId, LocalDateTime checkInDate, LocalDate checkOutDate, String DNI, String clientName, String clientSurname1, String clientSurname2) {
        this.id = id;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.clientDNI = DNI;
        this.clientName = clientName;
        this.clientSurname1 = clientSurname1;
        this.clientSurname2 = clientSurname2;
    }

    public BookingDTO(int id, int roomId, LocalDateTime checkInDate, LocalDate checkOutDate, String clientDNI, String clientName, String clientSurname1) {
        this.id = id;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.clientDNI = clientDNI;
        this.clientName = clientName;
        this.clientSurname1 = clientSurname1;
    }

    public BookingDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int room) {
        this.roomId = room;
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

    public void setClientSurname2(String surname2) {
        this.clientSurname2 = surname2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDTO that = (BookingDTO) o;
        return id == that.id && roomId == that.roomId && Objects.equals(checkInDate, that.checkInDate) && Objects.equals(checkOutDate, that.checkOutDate) && Objects.equals(clientDNI, that.clientDNI) && Objects.equals(clientName, that.clientName) && Objects.equals(clientSurname1, that.clientSurname1) && Objects.equals(clientSurname2, that.clientSurname2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomId, checkInDate, checkOutDate, clientDNI, clientName, clientSurname1, clientSurname2);
    }
}
