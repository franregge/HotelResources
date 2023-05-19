package com.imatia.campusdual.grupoun_bootcampbackend.model.entity;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingTest {

    @Test
    public void allArgsConstructor_validArguments_BookingWithCorrectFieldsIsCreated(){
        int bookingId=69;
        int hotelId = 1;
        String hotelName = "Overlook hotel";
        Integer numberOfFloors = 5;
        Hotel hotel = new Hotel(hotelId, hotelName, numberOfFloors);
        int roomId = 1;
        int roomNumber=101;
        Room room = new Room(roomId,roomNumber,hotel);
        LocalDateTime checkInDate = LocalDateTime.from(LocalDateTime.now().plusMonths(2));
        LocalDateTime checkOutDate = LocalDateTime.from(LocalDateTime.now().plusMonths(2).plusDays(3));
        String DNI= "12345678C";
        String userName="Juan";
        String userSurname1="Lopez";
        String userSurname2="Martinez";
        Booking booking= new Booking(bookingId,room,checkInDate,checkOutDate,DNI,userName,userSurname1,userSurname2);

        assertEquals(bookingId,booking.getId());
        assertEquals(userName,booking.getClientName());
        assertEquals(userSurname1,booking.getClientSurname1());
        assertEquals(userSurname2,booking.getClientSurname2());
        assertEquals(checkInDate,booking.getCheckInDate());
        assertEquals(checkOutDate,booking.getCheckOutDate());
        assertEquals(room,booking.getRoom());
    }/*
    @Test
    public void constructorWithOutSurname2_validArguments_bookingWithCorrectFieldsIsCreated(){
        int bookingId=69;
        int hotelId = 1;
        String hotelName = "Overlook hotel";
        Integer numberOfFloors = 5;
        Hotel hotel = new Hotel(hotelId, hotelName, numberOfFloors);
        int roomId = 1;
        int roomNumber=101;
        Room room = new Room(roomId,roomNumber,hotel);

        LocalDateTime checkInDate = LocalDateTime.from(LocalDateTime.now().plusMonths(2));
        LocalDateTime checkOutDate = LocalDateTime.from(LocalDateTime.now().plusMonths(2).plusDays(3));
        String DNI= "12345678C";
        String userName="Juan";
        String userSurname1="Lopez";

        Booking booking= new Booking(bookingId,room,checkInDate,checkOutDate,DNI,userName,userSurname1);

        assertEquals(bookingId,booking.getId());
        assertEquals(userName,booking.getClientName());
        assertEquals(userSurname1,booking.getClientSurname1());
        assertEquals(checkInDate,booking.getCheckInDate());
        assertEquals(checkOutDate,booking.getCheckOutDate());
        assertEquals(room,booking.getRoom());

    }*/
}
