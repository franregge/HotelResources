package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IHotelService;
import com.ontimize.hr.api.core.service.exception.HotelAlreadyExistsException;
import com.ontimize.hr.api.core.service.exception.HotelDoesNotExistException;
import com.ontimize.hr.api.core.service.exception.InvalidFloorNumberException;
import com.ontimize.hr.api.core.service.exception.InvalidNumberOfFloorsException;
import com.ontimize.hr.model.core.dao.HotelDAO;
import com.ontimize.hr.model.core.dao.RoomDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    //Sample to permission
    //@Secured({ PermissionsProviderSecured.SECURED })

    @Override
    public EntityResult hotelQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(hotelDAO, keyMap, attrList);
    }

    @Override
    public EntityResult hotelInsert(Map<?, ?> attrMap) throws HotelAlreadyExistsException, InvalidNumberOfFloorsException {
        Map<String,String>filter =new HashMap<>();
        filter.put("name",(String) attrMap.get("name"));
        List<String>attrList= List.of("name");
        EntityResult entityResult = hotelQuery(filter,attrList);

        if (entityResult.calculateRecordNumber()>0) {
            throw new HotelAlreadyExistsException("This hotel already exists");
        }

        if ((int)attrMap.get("NUMBER_OF_FLOORS") > 9 || (int)attrMap.get("NUMBER_OF_FLOORS") < 1) {
            throw new InvalidNumberOfFloorsException("The number of floors must be between 1 and 9");
        }

        return this.daoHelper.insert(hotelDAO, attrMap);
    }

    @Override
    public EntityResult hotelUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) throws Exception {
        // TODO error messages
        Map<String, Integer> filter = new HashMap<>();
        Integer hotelId = (Integer) keyMap.get("id");

        if (hotelId == null) {
            throw new HotelDoesNotExistException("You must provide a hotel id");
        }

        filter.put("id", hotelId);
        EntityResult hotelEntityResult = hotelQuery(filter, List.of("number_of_floors"));

        if (hotelEntityResult.calculateRecordNumber() == 0) {
            throw new HotelDoesNotExistException("No hotel with the specified id could be found");
        }

        //Validación nombre no nulo ni vacío
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
        EntityResult hotelRoomsEntityResult = roomService.roomQuery(filter, List.of("room_number"));

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

        EntityResult result = this.daoHelper.update(hotelDAO, attrMap, keyMap);
        result.setMessage("Hotel updated successfully");
        result.put("updated_id", hotelId);
        return result;
    }

}
