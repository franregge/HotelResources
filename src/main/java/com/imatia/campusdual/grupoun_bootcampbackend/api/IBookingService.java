package com.imatia.campusdual.grupoun_bootcampbackend.api;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.BookingDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.BookingAlreadyExsistsException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.BookingDoesNotExistsException;


import java.util.List;

public interface IBookingService {

    BookingDTO queryBooking(BookingDTO bookingDTO);
    List<BookingDTO>queryAll();
    int insertBooking(BookingDTO bookingDTO) throws BookingAlreadyExsistsException;
    int deleteBooking(BookingDTO bookingDTO) throws BookingDoesNotExistsException;
   /* int updateBooking(BookingDTO bookingDTO) throws  BookingDoesNotExistsException;*/
}
