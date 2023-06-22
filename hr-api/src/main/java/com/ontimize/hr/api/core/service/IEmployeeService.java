package com.ontimize.hr.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IEmployeeService {

    EntityResult employeeQuery(Map<?, ?> filter, List<?> attrList);

    EntityResult employeeInsert(Map<? super Object, ? super Object> attrMap);

    EntityResult employeeDelete(Map<?, ?> keyMap);

    EntityResult employeeUpdate(Map<? super Object, ? super Object> attrMap, Map<? super Object, ? super Object> filter);

    String ERR_CANNOT_DELETE_USER = "Cannot delete this user";

}
