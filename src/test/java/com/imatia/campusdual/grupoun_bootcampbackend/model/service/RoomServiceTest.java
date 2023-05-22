package com.imatia.campusdual.grupoun_bootcampbackend.model.service;

import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.HotelDAO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dao.RoomDAO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.RoomDTO;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper.HotelMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.dto.dtomapper.RoomMapper;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Hotel;
import com.imatia.campusdual.grupoun_bootcampbackend.model.entity.Room;
import com.imatia.campusdual.grupoun_bootcampbackend.service.HotelService;
import com.imatia.campusdual.grupoun_bootcampbackend.service.RoomService;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.InvalidAssignedHotelException;
import com.imatia.campusdual.grupoun_bootcampbackend.service.exception.InvalidRoomNumberException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoomServiceTest {

    @MockBean
    RoomDAO roomDAO;
    @MockBean
    HotelService hotelService;
    @Autowired
    HotelMapper hotelMapper;
    @Autowired
    ApplicationContext context;
    @Autowired
    RoomMapper roomMapper;
    RoomService roomService;
    @Autowired
    private HotelDAO hotelDAO;
    Hotel hotel;

    @BeforeAll
    public void init() {
        roomService = context.getBean(RoomService.class);
        String name = "Overlook Hotel";
        Integer numberOfFloors = 6;
        Integer idHotel = 1;
        hotel = new Hotel(idHotel, name, numberOfFloors);

    }

    @Test
    public void insertRoom_roomIsSaved() throws InvalidRoomNumberException, InvalidAssignedHotelException {
        int id = 1;
        int roomNumber = 101;
        Room room = new Room(id, roomNumber, hotel);

        when(roomDAO.getReferenceById(id)).thenReturn(new Room(id, roomNumber, hotel));
        when(hotelService.queryAll()).thenReturn(List.of(hotelMapper.toDTO(hotel)));
        when(hotelService.queryHotel(any())).thenReturn(hotelMapper.toDTO(hotel));
        when(roomDAO.saveAndFlush(any())).thenReturn(room);
        when(roomDAO.findAll()).thenReturn(new ArrayList<>());

        assertEquals(room.getId(), roomService.insertRoom(roomMapper.toDTO(room)));
        assertEquals(roomMapper.toDTO(room), roomService.queryRoom(roomMapper.toDTO(room)));
    }

    @Test
    public void deleteRoom_roomIsDeleted() {
        int id = 1;
        int roomNumber = 101;
        RoomDTO roomDTO = new RoomDTO(id, roomNumber);
        Room room = new Room(id, roomNumber, hotel);

        when(roomDAO.getReferenceById(id)).thenReturn(room);

        int deletedId = roomService.deleteRoom(roomDTO);

        verify(roomDAO, times(1)).delete(any(Room.class));
        assertEquals(roomDTO.getId(), deletedId);

    }

    @Test
    public void getAllRoomsTest() {
        List<Room> roomList = new ArrayList<>();

        Room room1 = new Room(1, 101, hotel);
        Room room2 = new Room(2, 102, hotel);
        Room room3 = new Room(3, 103, hotel);

        roomList.add(room1);
        roomList.add(room2);
        roomList.add(room3);
        when(roomDAO.findAll()).thenReturn(roomList);
        List<RoomDTO> empList = this.roomService.queryAll();

        verify(this.roomDAO, times(1)).findAll();
        assertEquals(3, empList.size());

    }

    @Test
    public void getRoomByIdTest() {
        Room room = new Room(1, 101, hotel);

        when(this.roomDAO.getReferenceById(1)).thenReturn(room);

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1);

        RoomDTO roomResult = this.roomService.queryRoom(roomDTO);

        assertNotNull(roomResult);
    }

}
