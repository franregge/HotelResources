package com.imatia.campusdual.grupoun_bootcampbackend.service;

import com.imatia.campusdual.grupoun_bootcampbackend.api.IBookingService;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.BookingDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.BookingDAO;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper.BookingMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Booking;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.BookingAlreadyExistsException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.BookingDoesNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("BookingService")
@Lazy
public class BookingService implements IBookingService {
    @Autowired
    BookingDAO bookingDAO;
    @Autowired
    BookingMapper bookingMapper;


    @Override
    public BookingDTO queryBooking(BookingDTO bookingDTO) {
        Booking booking= bookingDAO.getReferenceById(bookingDTO.getId());
        return bookingMapper.toDTO(booking);
    }

    @Override
    public List<BookingDTO> queryAll() {
        return bookingMapper.toDTOList(bookingDAO.findAll());
    }

    @Override
    public int insertBooking(BookingDTO bookingDTO) throws BookingAlreadyExistsException {
        //TODO: HOTEL AVALIABLE, ROOM AVALIABLE , DATE AVALIABLE.
        List<BookingDTO>allBookingDTOs = queryAll();
        if (allBookingDTOs.stream().anyMatch((dto->dto.getId()==(bookingDTO.getId())))){
            throw new BookingAlreadyExistsException("This booking already exists");
        }
        Booking booking= bookingMapper.toEntity(bookingDTO);

        booking=bookingDAO.saveAndFlush(booking);
        return booking.getId();
    }

    @Override
    public int deleteBooking(BookingDTO bookingDTO) throws BookingDoesNotExistsException {
        if(queryBooking(bookingDTO)==null){
            throw new BookingDoesNotExistsException("Hotel not found");
        }
        bookingDAO.deleteById(bookingDTO.getId());

        return bookingDTO.getId();
    }

   /* @Override
    public int updateBooking(BookingDTO bookingDTO) throws BookingDoesNotExistsException {
        //TODO: HOTEL AVALIABLE, ROOM AVALIABLE , DATE AVALIABLE.
        List<BookingDTO>allBookingDTOs= queryAll();
        if(allBookingDTOs.stream().noneMatch(dto->dto.getId()==bookingDTO.getId())){
            new BookingDoesNotExistsException("The booking does not exist");
        }

        bookingDAO.deleteById(bookingDTO.getId());

        return bookingDTO.getId();
    }*/
}
