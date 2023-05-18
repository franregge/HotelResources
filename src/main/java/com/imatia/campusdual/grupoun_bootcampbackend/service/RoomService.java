package com.imatia.campusdual.grupoun_bootcampbackend.service;

import com.imatia.campusdual.grupoun_bootcampbackend.api.IRoomService;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.RoomDAO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.HotelDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.RoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper.HotelMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper.RoomMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Room;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.HotelDoesNotExistException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.InvalidAssignedHotelException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.InvalidRoomNumberException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("RoomService")
@Lazy
public class RoomService implements IRoomService {

    private final RoomDAO roomDAO;
    private final HotelService hotelService;
    private final RoomMapper roomMapper;
    private final HotelMapper hotelMapper;
    private final int FIRST_ROOM_NUMBER = 101; // No rooms in the ground floor

    public RoomService(RoomDAO roomDAO, HotelService hotelService, RoomMapper roomMapper, HotelMapper hotelMapper) {
        this.roomDAO = roomDAO;
        this.hotelService = hotelService;
        this.roomMapper = roomMapper;
        this.hotelMapper = hotelMapper;
    }

    @Override
    public int insertRoom(RoomDTO roomDTO) throws InvalidAssignedHotelException, InvalidRoomNumberException {
        int assignedHotelId = roomDTO.getHotelId();

        if (assignedHotelId == 0 || hotelService.queryAll().stream().noneMatch(hotel -> hotel.getId() == assignedHotelId)) {
            throw new InvalidAssignedHotelException("The assigned hotel does not exist");
        }

        // TODO: Hotels have 9 floors at most, hotel creation validation needed (?)
        int floorNumber = roomDTO.getFloorNumber();
        HotelDTO assignedHotelDTO = new HotelDTO();
        assignedHotelDTO.setId(assignedHotelId);
        assignedHotelDTO = hotelService.queryHotel(assignedHotelDTO);
        if (
                roomDTO.getRoomNumber() < FIRST_ROOM_NUMBER ||
                        floorNumber > assignedHotelDTO.getNumberOfFloors() ||
                        queryAll()
                                .stream()
                                .anyMatch(
                                        room -> room.getRoomNumber() == roomDTO.getRoomNumber() &&
                                                room.getHotelId() == roomDTO.getHotelId()
                                )
        ) {
            throw new InvalidRoomNumberException("Cannot create room with this number");
        }

        Hotel assignedHotel = hotelMapper.toEntity(hotelService.queryHotel(assignedHotelDTO));
        Room room = roomMapper.toEntity(roomDTO);
        room.setHotel(assignedHotel);
        room = roomDAO.saveAndFlush(room);
        assignedHotel.getRooms().add(room);
        try {
            hotelService.updateHotel(hotelMapper.toDTO(assignedHotel));
        } catch (HotelDoesNotExistException e) {
            throw new RuntimeException(e);
        }
        return room.getId();
    }


    @Override
    public int deleteRoom(RoomDTO roomDTO) {
        Room room = roomMapper.toEntity(roomDTO);
        roomDAO.delete(room);
        return roomDTO.getId();
    }

    @Override
    public RoomDTO queryRoom(RoomDTO roomDTO) {
        Room room = roomDAO.getReferenceById(roomDTO.getId());

        return roomMapper.toDTO(room);
    }

    @Override
    public List<RoomDTO> queryAll() {
        return roomMapper.toDTOList(roomDAO.findAll());
    }

}