package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IRoomService;
import com.ontimize.hr.api.core.service.exception.InvalidAssignedHotelException;
import com.ontimize.hr.api.core.service.exception.InvalidRoomNumberException;
import com.ontimize.hr.model.core.dao.RoomDAO;
import com.ontimize.hr.model.core.util.RoomUtils;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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
    public int roomInsert(Map<?, ?> attrMap) throws InvalidAssignedHotelException, InvalidRoomNumberException {
        int assignedHotelId = (int) attrMap.get(RoomDAO.HOTEL_ID);

        if (assignedHotelId == 0 || !hotelService.equals(assignedHotelId)) {
            throw new InvalidAssignedHotelException("The assigned hotel does not exist");
        }

        if (!validateRoomNumber(attrMap.size(), assignedHotelId)) {
            throw new InvalidRoomNumberException("Cannot create room with this number");
        }

        attrMap.put("hotel_id", assignedHotelId);
        EntityResult result = this.daoHelper.insert(this.roomDAO, attrMap);
        int roomId = result.getCode();

        return result.calculateRecordNumber();
    }

    @Override
    public EntityResult roomUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        return this.daoHelper.update(this.roomDAO, attrMap, keyMap);
    }

}
