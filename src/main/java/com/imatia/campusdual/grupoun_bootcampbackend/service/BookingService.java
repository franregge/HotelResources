package com.imatia.campusdual.grupoun_bootcampbackend.service;

import com.imatia.campusdual.grupoun_bootcampbackend.api.IBookingService;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.BookingDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.BookingDAO;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.RoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper.BookingMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Booking;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Room;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("BookingService")
@Lazy
public class BookingService implements IBookingService {
    @Autowired
    BookingDAO bookingDAO;
    @Autowired
    BookingMapper bookingMapper;
    @Autowired
    RoomService roomService;

    @Override
    public BookingDTO queryBooking(BookingDTO bookingDTO) {
        Booking booking = bookingDAO.getReferenceById(bookingDTO.getId());
        return bookingMapper.toDTO(booking);
    }

    @Override
    public List<BookingDTO> queryAll() {
        return bookingMapper.toDTOList(bookingDAO.findAll());
    }

    @Override
    public int insertBooking(BookingDTO bookingDTO) throws InvalidBookingDateException, RoomNotAvailableException, RoomDoesNotExistException, InvalidBookingDNIException {
        //validación fecha
        if (bookingDTO.getCheckInDate().isBefore(LocalDateTime.now()) || bookingDTO.getCheckOutDate().isBefore(bookingDTO.getCheckInDate().toLocalDate())) {
            throw new InvalidBookingDateException("This date is not valid");
        }

        if (!validateDNI(bookingDTO.getClientDNI())) {
            throw new InvalidBookingDNIException("The DNI format is not correct");
        }

        //validación existencia Room
        List<RoomDTO> allRoomDTOs = roomService.queryAll();

        if (allRoomDTOs.stream().noneMatch(dto -> dto.getId() == bookingDTO.getRoomId())) {
            throw new RoomDoesNotExistException("Room does not exist");
        }


        List<BookingDTO> allBookingDTOs = queryAll();
        if (allBookingDTOs.stream().anyMatch(dto ->
                (bookingDTO.getRoomId() == dto.getRoomId()) &&
                        ((bookingDTO.getCheckInDate().isAfter(dto.getCheckInDate()) || bookingDTO.getCheckInDate().toLocalDate().isEqual(dto.getCheckInDate().toLocalDate())) &&
                                (bookingDTO.getCheckOutDate().isBefore(dto.getCheckOutDate()) || bookingDTO.getCheckOutDate().isEqual(dto.getCheckOutDate())))
        )) {
            throw new RoomNotAvailableException("This date is already taken");
        }


        Booking booking = bookingMapper.toEntity(bookingDTO);

        booking = bookingDAO.saveAndFlush(booking);
        return booking.getId();
    }

    @Override
    public int deleteBooking(BookingDTO bookingDTO) throws BookingDoesNotExistsException {
        if (queryBooking(bookingDTO) == null) {
            throw new BookingDoesNotExistsException("Hotel not found");
        }
        bookingDAO.deleteById(bookingDTO.getId());

        return bookingDTO.getId();
    }

    public boolean validateDNI(String dni) {
        List<Character> letters = List.of(
                'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H',
                'L', 'C', 'K', 'E'
        );

        if (dni.length() != 9) {
            return false;
        }

        dni = dni.toUpperCase();

        if (!dni.matches("[0-9]{8}[A-HJ-NP-TV-Z]")) {
            return false;
        }

        int numberSegment = Integer.parseInt(dni.substring(0, 7));
        char letter = dni.charAt(8);

        return letters.get(numberSegment % 23) != letter;
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
