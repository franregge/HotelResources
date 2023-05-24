package com.ontimize.hr.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IHotelService {

    public EntityResult hotelQuery(Map<?, ?> keyMap, List<?> attrList);
    public EntityResult hotelInsert(Map<?, ?> attrMap);
    public EntityResult hotelUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap);
}
