package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IEmployeeService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.jee.common.dto.EntityResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Lazy
@Service("EmployeeService")
public class EmployeeService implements IEmployeeService {

    @Autowired
    private IUserService userService;

    @Override
    public EntityResult employeeQuery(Map<?, ?> keyMap, List<?> attrList) {
        return null;
    }

    @Override
    public EntityResult employeeInsert(Map<?, ?> attrMap) {
        return null;
    }

    @Override
    public EntityResult employeeDelete(Map<?, ?> keyMap) {
        return null;
    }

    @Override
    public EntityResult employeeUpdate(Map<?, ?> filter, Map<?, ?> attrMap) {
        return null;
    }
}
