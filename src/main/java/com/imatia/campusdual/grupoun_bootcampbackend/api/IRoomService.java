package com.imatia.campusdual.grupoun_bootcampbackend.api;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.RoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.InvalidAssignedHotelException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.InvalidRoomNumberException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.RoomDoesNotExistException;

import java.util.List;

public interface IRoomService {

    RoomDTO queryRoom(RoomDTO roomDTO);

    List<RoomDTO> queryAll();

    int insertRoom(RoomDTO roomDTO) throws InvalidAssignedHotelException, InvalidRoomNumberException;

    int deleteRoom(RoomDTO roomDTO);

    boolean roomExistsById(RoomDTO roomDTO);

    int updateRoom(RoomDTO roomDTO) throws InvalidRoomNumberException, InvalidAssignedHotelException, RoomDoesNotExistException;

}
