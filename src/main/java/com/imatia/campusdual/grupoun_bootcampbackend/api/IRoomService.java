package com.imatia.campusdual.grupoun_bootcampbackend.api;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.RoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.SimpleRoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Room;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.InvalidAssignedHotelException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.InvalidRoomNumberException;

import java.util.List;

public interface IRoomService {

    RoomDTO queryRoom(RoomDTO roomDTO);

    List<RoomDTO>queryAll();

    int insertRoom(RoomDTO roomDTO) throws InvalidAssignedHotelException, InvalidRoomNumberException;

    int deleteRoom(RoomDTO roomDTO);

}
