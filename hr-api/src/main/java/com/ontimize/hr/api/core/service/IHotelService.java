package com.ontimize.hr.api.core.service;

import com.ontimize.hr.api.core.service.exception.HotelAlreadyExistsException;
import com.ontimize.hr.api.core.service.exception.InvalidNumberOfFloorsException;
import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IHotelService {

    EntityResult hotelQuery(Map<?, ?> keyMap, List<?> attrList);

    EntityResult hotelInsert(Map<?, ?> attrMap) throws HotelAlreadyExistsException, InvalidNumberOfFloorsException;

    EntityResult hotelUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap);

    EntityResult hotelDelete(Map<?, ?> keyMap);

    String HOTEL_ALREADY_EXISTS_ERROR = "This hotel already exists";

}
