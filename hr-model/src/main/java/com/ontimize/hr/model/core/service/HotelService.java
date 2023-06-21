package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IHotelService;
import com.ontimize.hr.api.core.service.exception.HotelAlreadyExistsException;
import com.ontimize.hr.api.core.service.exception.HotelDoesNotExistException;
import com.ontimize.hr.api.core.service.exception.InvalidFloorNumberException;
import com.ontimize.hr.api.core.service.exception.InvalidNumberOfFloorsException;

import com.ontimize.hr.model.core.dao.HotelDAO;
import com.ontimize.hr.model.core.dao.RoomDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import com.ontimize.hr.model.core.util.RoomUtils;

import java.util.*;

@Lazy
@Service("HotelService")
public class HotelService implements IHotelService {

    @Autowired
    private HotelDAO hotelDAO;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomUtils roomUtils;

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult hotelQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(hotelDAO, keyMap, attrList);
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult hotelInsert(Map<?, ?> attrMap) {
        Map<String, String> filter = new HashMap<>();
        filter.put(HotelDAO.NAME, (String) attrMap.get(HotelDAO.NAME));
        List<String> attrList = List.of(HotelDAO.NAME);
        EntityResult entityResult = hotelQuery(filter, attrList);
        EntityResult result;

        try {
            if (entityResult.calculateRecordNumber() > 0) {
                throw new HotelAlreadyExistsException(IHotelService.HOTEL_ALREADY_EXISTS_ERROR);
            }

            if ((int) attrMap.get(HotelDAO.NUMBER_OF_FLOORS) > 99 || (int) attrMap.get(HotelDAO.NUMBER_OF_FLOORS) < 1) {
                throw new InvalidNumberOfFloorsException("The number of floors must be between 1 and 99");
            }

            result = this.daoHelper.insert(hotelDAO, attrMap);
            result.setMessage("Hotel saved successfully");
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);

        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
        }
        return result;
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    @SuppressWarnings("unchecked")
    public EntityResult hotelUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        Map<String, Integer> filter = new HashMap<>();
        Integer hotelId = (Integer) keyMap.get(HotelDAO.ID);
        EntityResult result;

        try {
            if (hotelId == null) {
                throw new HotelDoesNotExistException("You must provide a hotel id");
            }

            filter.put(HotelDAO.ID, hotelId);
            EntityResult hotelEntityResult = hotelQuery(filter, List.of(HotelDAO.NUMBER_OF_FLOORS));

            if (hotelEntityResult.calculateRecordNumber() == 0) {
                throw new HotelDoesNotExistException("No hotel with the specified id could be found");
            }


            if (attrMap.get(HotelDAO.NAME) != null && ((String) attrMap.get(HotelDAO.NAME)).isEmpty()) {

                throw new IllegalStateException("You must provide a non-empty name");
            }

            if (
                    attrMap.get(HotelDAO.NUMBER_OF_FLOORS) != null &&
                            (
                                    (int) attrMap.get(HotelDAO.NUMBER_OF_FLOORS) < 1 ||
                                            (int) attrMap.get(HotelDAO.NUMBER_OF_FLOORS) > 99
                            )
            ) {
                throw new IllegalStateException("Number of floors must be between 1 and 99");
            }

            Map<?, ?> hotelToUpdate = hotelEntityResult.getRecordValues(0);

            filter = new HashMap<>();
            filter.put(RoomDAO.HOTEL_ID, hotelId);
            EntityResult hotelRoomsEntityResult = roomService.roomQuery(filter, List.of(RoomDAO.ROOM_NUMBER));

            //Validación de habitaciones al borrar unha planta
            Integer newNumberOfFloors = (Integer) attrMap.get(HotelDAO.NUMBER_OF_FLOORS);

            if (
                    newNumberOfFloors != null &&
                            newNumberOfFloors < (Integer) hotelToUpdate.get(HotelDAO.NUMBER_OF_FLOORS) &&
                            ((List<Integer>) hotelRoomsEntityResult.get(RoomDAO.ROOM_NUMBER))
                                    .stream()
                                    .anyMatch(
                                            roomNumber -> roomUtils.getFloorNumber(roomNumber) > newNumberOfFloors
                                    )
            ) {
                throw new InvalidFloorNumberException("Invalid number of floors");
            }

            result = this.daoHelper.update(hotelDAO, attrMap, keyMap);
            result.setMessage("Hotel updated successfully");
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);

        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
        }

        return result;
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult hotelDelete(Map<?, ?> keyMap) {
        Integer hotelId = (Integer) keyMap.get(HotelDAO.ID);
        EntityResult result;

        if (this.daoHelper.query(hotelDAO, keyMap, List.of(HotelDAO.ID)).isEmpty()) {
            result = this.daoHelper.delete(hotelDAO, keyMap);
            result.setMessage("No hotels with this id");
            result.setCode(EntityResult.OPERATION_WRONG);
            return result;
        }

        result = this.daoHelper.delete(hotelDAO, keyMap);
        result.setMessage("Hotel deleted successfully");
        result.put("deleted_id", hotelId);
        result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
        return result;

    }

}
