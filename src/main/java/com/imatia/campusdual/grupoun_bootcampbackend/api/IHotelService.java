package com.imatia.campusdual.grupoun_bootcampbackend.api;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.HotelDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.*;

import java.util.List;

public interface IHotelService {

    HotelDTO queryHotel(HotelDTO hotelDTO);

    List<HotelDTO> queryAll();

    int insertHotel(HotelDTO hotelDTO) throws HotelAlreadyExistsException, InvalidNumberOfFloorsException, InvalidHotelNameException;

    int updateHotel(HotelDTO hotelDTO) throws HotelDoesNotExistException, InvalidFloorNumberException, IllegalStateException;

    int deleteHotel(HotelDTO hotelDTO) throws HotelDoesNotExistException;

}
