package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IEmployeeService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.model.core.RoleNames;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Lazy
@Service("EmployeeService")
public class EmployeeService implements IEmployeeService {

    @Autowired
    private UserService userService;

    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult employeeQuery(Map<?, ?> filter, List<?> attrList) {
        return userService.userIdentifiedQuery(filter, attrList, UserDAO.Q_EMPLOYEE_QUERY);
    }


    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult employeeInsert(Map<? super Object, ? super Object> attrMap) {
        attrMap.put(UserDAO.ROLE_NAME, RoleNames.EMPLOYEE);

        return userService.userInsert(attrMap);
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeeDelete(Map<?, ?> keyMap) {
        String loginName = (String) keyMap.get(UserDAO.LOGIN_NAME);
        EntityResult result;

        try {
            if (loginName == null) {
                throw new IllegalArgumentException("You must provide a username");
            }

            if (!userService.getUserRoles(loginName).contains(RoleNames.EMPLOYEE)) {
                throw new AccessDeniedException(ERR_CANNOT_DELETE_USER);
            }

            result = userService.userDelete(keyMap);
        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeeUpdate(Map<? super Object, ? super Object> attrMap, Map<? super Object, ? super Object> filter) {

        EntityResult result;

        try {
            if (!userService.getUserRoles((String) filter.get(UserDAO.LOGIN_NAME)).contains(RoleNames.EMPLOYEE)) {
                throw new AccessDeniedException(IUserService.WRONG_ROLE);
            }

            result = userService.userUpdate(attrMap, filter);

        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
