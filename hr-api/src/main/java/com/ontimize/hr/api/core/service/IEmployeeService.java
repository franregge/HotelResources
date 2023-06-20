package com.ontimize.hr.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IEmployeeService {

    EntityResult employeeQuery(Map<?, ?> keyMap, List<?> attrList);

    EntityResult employeeInsert(Map<? super Object, ? super Object> attrMap);

    EntityResult employeeDelete(Map<?, ?> keyMap) throws Exception;

    EntityResult employeeUpdate(Map<?, ?> filter, Map<?, ?> attrMap) throws Exception;

}
