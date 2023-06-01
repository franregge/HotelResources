package com.ontimize.hr.api.core.service;

import com.ontimize.hr.api.core.service.exception.InvalidNumberOfBeds;
import com.ontimize.hr.api.core.service.exception.InvalidPriceException;
import com.ontimize.hr.api.core.service.exception.InvalidRoomNumberException;
import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IRoomService {

    public EntityResult roomQuery(Map<?, ?> keymap, List<?> attrList);
    public EntityResult roomInsert(Map<?, ?> attrMap) throws InvalidRoomNumberException, InvalidNumberOfBeds, InvalidPriceException;
    public EntityResult roomUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) throws InvalidRoomNumberException, InvalidNumberOfBeds, InvalidPriceException;
}
