package com.ontimize.hr.api.core.service;

import com.ontimize.hr.api.core.service.exception.HotelAlreadyExistsException;
import com.ontimize.hr.api.core.service.exception.HotelDoesNotExistException;
import com.ontimize.hr.api.core.service.exception.InvalidNumberOfFloorsException;
import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IHotelService {

    public EntityResult hotelQuery(Map<?, ?> keyMap, List<?> attrList);
    public EntityResult hotelInsert(Map<?, ?> attrMap) throws HotelAlreadyExistsException, InvalidNumberOfFloorsException;
    public EntityResult hotelUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) throws Exception;
}
