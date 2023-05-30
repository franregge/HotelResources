package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IRoomService;
import com.ontimize.hr.api.core.service.exception.InvalidAssignedHotelException;
import com.ontimize.hr.api.core.service.exception.InvalidRoomNumberException;
import com.ontimize.hr.model.core.dao.HotelDAO;
import com.ontimize.hr.model.core.dao.RoomDAO;
import com.ontimize.hr.model.core.util.RoomUtils;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Lazy
@Service("RoomService")
public class RoomService implements IRoomService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private RoomDAO roomDAO;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RoomUtils roomUtils;

    private final int FIRST_ROOM_NUMBER = 101;

    @Override
    public EntityResult roomQuery(Map<?, ?> keymap, List<?> attrList) {
        return this.daoHelper.query(this.roomDAO, keymap, attrList);
    }

    public boolean validateRoomNumber(int roomNumber,int numberOfFloors) {

        return roomNumber >=FIRST_ROOM_NUMBER &&
                roomNumber <= 999 &&
                roomUtils.getFloorNumber(roomNumber) <= numberOfFloors;

    }

    @Override
    public EntityResult roomInsert(Map<?, ?> attrMap) {
        int assignedHotelId = (int) attrMap.get(RoomDAO.HOTEL_ID);

        Map<String, Integer> keyMap = new HashMap<>();

        EntityResult assignedHotelEntityResult =
       hotelService.hotelQuery();
        if (
                !validateRoomNumber(attrMap.get(RoomDAO.ROOM_NUMBER), assignedHotelDTO.getNumberOfFloors()) ||
                        roomDAO.existsByRoomNumberAndHotel_Id(roomDTO.getRoomNumber(), assignedHotelId)
        ) {
            throw new InvalidRoomNumberException("Cannot create room with this number");
        }

        Hotel assignedHotel = hotelMapper.toEntity(hotelService.queryHotel(assignedHotelDTO));
        Room room = roomMapper.toEntity(roomDTO);
        room.setHotel(assignedHotel);
        return this.daoHelper.insert(this.roomDAO, attrMap);
    }

    @Override
    public EntityResult roomUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        return this.daoHelper.update(this.roomDAO, attrMap, keyMap);
    }

}
