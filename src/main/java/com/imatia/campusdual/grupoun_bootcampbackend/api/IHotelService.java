package com.imatia.campusdual.grupoun_bootcampbackend.api;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.HotelDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.HotelAlreadyExistsException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.HotelDoesNotExistException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.InvalidFloorNumberException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.InvalidNumberOfFloorsException;

import java.util.List;

public interface IHotelService {

    HotelDTO queryHotel(HotelDTO hotelDTO);

    List<HotelDTO> queryAll();

    int insertHotel(HotelDTO hotelDTO) throws HotelAlreadyExistsException, InvalidNumberOfFloorsException;

    int updateHotel(HotelDTO hotelDTO) throws HotelDoesNotExistException, InvalidFloorNumberException, IllegalStateException;

    int deleteHotel(HotelDTO hotelDTO) throws HotelDoesNotExistException;

}
