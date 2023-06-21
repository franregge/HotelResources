package com.ontimize.hr.api.core.service;

import com.ontimize.hr.api.core.service.exception.InvalidNumberOfBeds;
import com.ontimize.hr.api.core.service.exception.InvalidPriceException;
import com.ontimize.hr.api.core.service.exception.InvalidRoomNumberException;
import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IRoomService {

    EntityResult roomQuery(Map<?, ?> keymap, List<?> attrList);

    EntityResult roomInsert(Map<?, ?> attrMap) throws InvalidRoomNumberException, InvalidNumberOfBeds, InvalidPriceException;

    EntityResult roomUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) throws InvalidRoomNumberException, InvalidNumberOfBeds, InvalidPriceException;

}
