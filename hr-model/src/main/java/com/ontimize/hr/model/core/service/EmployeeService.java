package com.ontimize.hr.model.core.service;

import com.ontimize.hr.api.core.service.IEmployeeService;
import com.ontimize.hr.api.core.service.IShiftService;
import com.ontimize.hr.api.core.service.IUserService;
import com.ontimize.hr.api.core.service.exception.InvalidShiftException;
import com.ontimize.hr.model.core.RoleNames;
import com.ontimize.hr.model.core.dao.UserDAO;
import com.ontimize.hr.model.core.dao.UsersDaysOffDAO;
import com.ontimize.hr.model.core.dao.UserRoleDAO;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Lazy
@Service("EmployeeService")
public class EmployeeService implements IEmployeeService {

    @Autowired
    private UserService userService;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private UserRoleDAO userRoleDAO;

    @Autowired
    private UsersDaysOffDAO usersDaysOffDAO;



    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult employeeQuery(Map<?, ?> filter, List<?> attrList) {
        return userService.userIdentifiedQuery(filter, attrList, UserDAO.Q_EMPLOYEE_QUERY);
    }


    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult employeeInsert(Map<? super Object, ? super Object> attrMap) throws InvalidShiftException {
        EntityResult result;

        attrMap.put(UserDAO.ROLE_NAME, RoleNames.EMPLOYEE);

        List<String> daysOff = (List<String>) attrMap.get(UserDAO.DAYS_OFF);

        List<String> days = List.of(
                "monday","tuesday","wednesday","thursday","friday","saturday","sunday"
        );

        if (daysOff !=null) {

            if (daysOff.isEmpty()){
                throw new InvalidShiftException(IShiftService.NO_DAYS_OFF);
            }

            if(new HashSet<>(daysOff).containsAll(days)){
                throw new InvalidShiftException(IShiftService.ALL_DAYS_OFF);
            }

            Map<String, String> daysOffToInsert = new HashMap<>();

            for (String dayOff : daysOff) {

                if(!days.contains(dayOff)){
                    throw new InvalidShiftException(IShiftService.INVALID_DAY_OFF);
                }
                daysOffToInsert.put(UserDAO.LOGIN_NAME, dayOff);
                daoHelper.insert(this.usersDaysOffDAO, daysOffToInsert);
            }


        }else{
            throw new InvalidShiftException(IShiftService.NO_DAYS_OFF);
        }

        try {
            Map<String, String> filter = new HashMap<>();
            filter.put(UserRoleDAO.NAME, RoleNames.EMPLOYEE);
            List<String> queriedAttrs = List.of(UserRoleDAO.ID);
            EntityResult roleQueryEntityResult = daoHelper.query(userRoleDAO, filter, queriedAttrs);
            Integer roleId = (Integer) roleQueryEntityResult.getRecordValues(0).get(UserRoleDAO.ID);
            attrMap.put(UserDAO.ROLE_ID, roleId);

            result = userService.userInsert(attrMap);
            result.setCode(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE);
            result.setMessage(IUserService.USER_INSERT_SUCCESS);
        } catch (Exception e) {
            result = new EntityResultMapImpl();
            result.setMessage(e.getMessage());
            result.setCode(EntityResult.OPERATION_WRONG);
            e.printStackTrace();
        }

        return result;
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
