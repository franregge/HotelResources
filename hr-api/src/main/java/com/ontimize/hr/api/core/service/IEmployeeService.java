package com.ontimize.hr.api.core.service;

import com.ontimize.hr.api.core.service.exception.InvalidShiftException;
import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IEmployeeService {

    EntityResult employeeQuery(Map<?, ?> filter, List<?> attrList);

    EntityResult employeeInsert(Map<? super Object, ? super Object> attrMap) throws InvalidShiftException;

    EntityResult employeeDelete(Map<?, ?> keyMap);

    EntityResult employeeUpdate(Map<?, ?> filter, Map<?, ?> attrMap);

}
