package com.ontimize.hr.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IShiftService {

    EntityResult shiftQuery(Map<?, ?> keyMap, List<?> attrList);

    EntityResult shiftInsert(Map<? super Object, ? super Object> attrMap);

    EntityResult shiftUpdate(Map<? super Object, ? super  Object> attrMap, Map<?, ?> keyMap);

    EntityResult shiftDelete(Map<?, ?> keyMap);

    String E_MORE_THAN_40H = "Employees cannot work more than 40 hours in a week";
    String E_EMPLOYEE_ROLE_MISMATCH = "Employee role has to match shift role";
    String OPERATION_SUCCESS = "Shift inserted successfully";
}
