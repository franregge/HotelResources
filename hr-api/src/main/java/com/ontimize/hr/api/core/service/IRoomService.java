package com.ontimize.hr.api.core.service;

import com.ontimize.hr.api.core.service.exception.InvalidNumberOfBeds;
import com.ontimize.hr.api.core.service.exception.InvalidPriceException;
import com.ontimize.hr.api.core.service.exception.InvalidRoomNumberException;
import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IRoomService {

    EntityResult roomQuery(Map<?, ?> keymap, List<?> attrList);

    EntityResult roomInsert(Map<?, ?> attrMap);

    EntityResult roomUpdate(Map<? super Object, ? super Object> attrMap, Map<? super Object, ? super Object> keyMap);

    EntityResult roomDelete(Map<? super Object, ? super Object> filter);

    String M_UPDATE_SUCCESS = "Room updated successfully";

}
