package com.imatia.campusdual.grupoun_bootcampbackend.api;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.BookingDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.*;


import java.util.List;

public interface IBookingService {

    BookingDTO queryBooking(BookingDTO bookingDTO);

    List<BookingDTO> queryAll();

    int insertBooking(BookingDTO bookingDTO) throws BookingAlreadyExistsException, InvalidBookingDateException, RoomNotAvailableException, RoomDoesNotExistException, InvalidBookingDNIException;

    int deleteBooking(BookingDTO bookingDTO) throws BookingDoesNotExistsException;
    /* int updateBooking(BookingDTO bookingDTO) throws  BookingDoesNotExistsException;*/
}
