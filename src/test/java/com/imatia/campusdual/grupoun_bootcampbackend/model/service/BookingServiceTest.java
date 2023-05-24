package com.imatia.campusdual.grupoun_bootcampbackend.model.service;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.BookingDAO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.RoomDAO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.BookingDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper.BookingMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper.RoomMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Booking;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Room;
import com.imatia.campusdual.grupoun_bootcampbackend.service.BookingService;
import com.imatia.campusdual.grupoun_bootcampbackend.service.RoomService;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingServiceTest {
    @MockBean
    BookingDAO bookingDAO;
    @Autowired
    ApplicationContext context;
    @Autowired
    BookingMapper bookingMapper;
    @MockBean
    RoomService roomService;
    @Autowired
    RoomMapper roomMapper;
    BookingService bookingService;
    Booking booking;
    Booking booking2;
    List<Booking> bookingList;

    @BeforeAll
    public void init() {
        bookingService = context.getBean(BookingService.class);
    }

    @BeforeEach
    public void bookings() {
        int id = 1;
        Hotel hotel = new Hotel(1, "Overlook Hotel", 5);
        Room room = new Room(1, 101, hotel);
        LocalDateTime checkInDate = LocalDateTime.now().plusMonths(2);
        LocalDate checkOutDate = LocalDate.now().plusMonths(2).plusDays(2);
        String dni = "06277345R";
        String name = "Roberto";
        String surname1 = "Williams";
        booking = new Booking(
                id,
                room,
                checkInDate,
                checkOutDate,
                dni,
                name,
                surname1
        );

        int id2 = 2;
        Room room2 = new Room(2, 102, hotel);
        LocalDateTime checkInDate2 = LocalDateTime.now().plusMonths(3);
        LocalDate checkOutDate2 = LocalDate.now().plusMonths(3).plusDays(2);
        String dni2 = "03022972J";
        String name2 = "Roberta";
        String surname1_2 = "Williams";
        booking2 = new Booking(
                id2,
                room2,
                checkInDate2,
                checkOutDate2,
                dni2,
                name2,
                surname1_2
        );

        bookingList = List.of(booking, booking2);
    }

    @Test
    public void queryBooking_existingBooking_returnsCorrectBooking() {
        int id = 1;
        BookingDTO bookingDTO = bookingMapper.toDTO(booking);

        when(bookingDAO.getReferenceById(id)).thenReturn(booking);

        assertEquals(booking.getId(), bookingService.queryBooking(bookingDTO).getId());
    }

    @Test
    public void queryAll_returnsAllBookings() {
        when(bookingDAO.findAll()).thenReturn(bookingList);
        List<BookingDTO> bookingDTOs = bookingService.queryAll();

        assertTrue(bookingDTOs.stream().anyMatch(dto -> dto.getId() == booking.getId()));
        assertTrue(bookingDTOs.stream().anyMatch(dto -> dto.getId() == booking2.getId()));
    }

    @Test
    public void insertBooking_validBooking_bookingIsSaved() throws RoomDoesNotExistException, InvalidBookingDateException, RoomNotAvailableException, InvalidBookingDNIException {
        int id = 1;
        when(bookingDAO.getReferenceById(id)).thenReturn(booking);
        when(bookingDAO.saveAndFlush(any())).thenReturn(booking);
        when(roomService.queryAll()).thenReturn(List.of(roomMapper.toDTO(booking.getRoom())));
        when(roomService.roomExistsById(any())).thenReturn(true);

        assertEquals(bookingMapper.toDTO(booking), bookingService.queryBooking(bookingMapper.toDTO(booking)));
        assertEquals(booking.getId(), bookingService.insertBooking(bookingMapper.toDTO(booking)));
    }

    @Test
    public void deleteBooking_existingBooking_bookingIdIsReturned() throws BookingDoesNotExistException {
        int id = 1;
        when(bookingDAO.getReferenceById(id)).thenReturn(booking);

        assertEquals(id, bookingService.deleteBooking(bookingMapper.toDTO(booking)));
    }

    @Test
    public void validateDNI_validDNI_returnsTrue() {
        assertTrue(bookingService.validateDNI("03022972J"));
    }

    @Test
    public void validateDNI_invalidDNI_returnsFalse() {
        assertFalse(bookingService.validateDNI("03022972A"));
    }
}
