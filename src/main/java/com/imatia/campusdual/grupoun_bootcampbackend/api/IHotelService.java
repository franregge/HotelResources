package com.imatia.campusdual.grupoun_bootcampbackend.api;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.HotelDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.HotelAlreadyExistsException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.HotelDoesNotExistException;

import java.util.List;

public interface IHotelService {

    HotelDTO queryHotel(HotelDTO hotelDTO);

    List<HotelDTO> queryAll();

    int insertHotel(HotelDTO hotelDTO) throws HotelAlreadyExistsException;

    int updateHotel(HotelDTO hotelDTO) throws HotelDoesNotExistException;

    int deleteHotel(HotelDTO hotelDTO) throws HotelDoesNotExistException;

}
