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

import java.util.ArrayList;
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

    public boolean validateRoomNumberOverMinimum(int roomNumber) {
        return roomNumber >= FIRST_ROOM_NUMBER;
    }

    public boolean validateRoomNumberRoomNonZero(int roomNumber) {
        return roomNumber % 10 != 0;
    }

    public boolean validateRoomNumberUnderMaximum(int roomNumber) {
        return roomNumber <= 9999;
    }

    public boolean validateRoomNumberEnoughFloors(int roomNumber, int numberOfFloors) {
        return roomUtils.getFloorNumber(roomNumber) <= numberOfFloors;
    }

    public void validateRoomNumber(int roomNumber, int numberOfFloors) throws InvalidRoomNumberException {
        if (!validateRoomNumberOverMinimum(roomNumber)) {
            throw new InvalidRoomNumberException("The roomNumber has to be over 100");
        }
        if (!validateRoomNumberUnderMaximum(roomNumber)) {
            throw new InvalidRoomNumberException("The roomNumber has to be under 10000");
        }
        if (!validateRoomNumberEnoughFloors(roomNumber, numberOfFloors)) {
            throw new InvalidRoomNumberException("This hotel has not enough floors");
        }
        if (!validateRoomNumberRoomNonZero(roomNumber)){
            throw new InvalidRoomNumberException("The roomNumber cannot finish with 0");
        }
    }

        @Override
        public EntityResult roomInsert (Map < ?, ?>attrMap) throws InvalidRoomNumberException {
            int assignedHotelId = (int) attrMap.get(RoomDAO.HOTEL_ID);


            Map<String, Integer> keyMap = new HashMap<>();

            EntityResult assignedHotelEntityResult =
                    hotelService.hotelQuery(keyMap, List.of(HotelDAO.NUMBER_OF_FLOORS));

            validateRoomNumber((int) attrMap.get(RoomDAO.ROOM_NUMBER), ((List<Integer>) assignedHotelEntityResult.get(HotelDAO.NUMBER_OF_FLOORS)).get(0));

            return this.daoHelper.insert(this.roomDAO, attrMap);
        }
        public EntityResult roomDelete (Map < ?, ?>keyMap) throws Exception {
            Integer roomId = (Integer) keyMap.get(RoomDAO.ID);

            //if (!this.daoHelper.query(hotelDAO, keyMap, List.of("hotel_id"),HotelDAO.QUERY_BOOKINGS_IN_HOTEL).isEmpty()){
            //    throw new Exception("This hotel has booked rooms");
            //}

            if (this.daoHelper.query(roomDAO, keyMap, List.of(RoomDAO.ID)).isEmpty()) {
                EntityResult result = this.daoHelper.delete(roomDAO, keyMap);
                result.setMessage("No rooms with this id");
                result.setCode(EntityResult.OPERATION_WRONG);
                return result;
            }

            EntityResult result = this.daoHelper.delete(roomDAO, keyMap);
            result.setMessage("Room deleted successfully");
            result.put("deleted_id", roomId);
            return result;
        }

        @Override
        public EntityResult roomUpdate (Map < ?, ?>attrMap, Map < ?, ?>keyMap) throws InvalidRoomNumberException {

            EntityResult roomEntityResult = roomQuery(keyMap, List.of(RoomDAO.HOTEL_ID));
            int assignedHotelId = ((List<Integer>) roomEntityResult.get(RoomDAO.HOTEL_ID)).get(0);

            Map<String, Integer> filter = new HashMap<>();
            filter.put(RoomDAO.HOTEL_ID, assignedHotelId);

            EntityResult assignedHotelEntityResult =
                    hotelService.hotelQuery(filter, List.of(HotelDAO.NUMBER_OF_FLOORS));

            if (
                    !validateRoomNumber((int) attrMap.get(RoomDAO.ROOM_NUMBER), ((List<Integer>) assignedHotelEntityResult.get(HotelDAO.NUMBER_OF_FLOORS)).get(0))
            ) {
                throw new InvalidRoomNumberException("Cannot update the room with this number, not enough floors");
            }

            return this.daoHelper.update(this.roomDAO, attrMap, keyMap);
        }

    }
