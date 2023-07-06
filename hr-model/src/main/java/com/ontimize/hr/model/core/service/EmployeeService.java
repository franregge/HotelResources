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

import java.util.*;

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


    @SuppressWarnings("unchecked")
    @Secured({PermissionsProviderSecured.SECURED})
    @Override
    public EntityResult employeeInsert(Map<? super Object, ? super Object> attrMap) {
        EntityResult result;

        attrMap.put(UserDAO.ROLE_NAME, RoleNames.EMPLOYEE);

        Set<String> daysOff = attrMap.get(UserDAO.DAYS_OFF) == null ? null : new HashSet<>((List<String>) attrMap.get(UserDAO.DAYS_OFF));

        Set<String> days = new HashSet<>(List.of(
                "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"
        ));

        try {
            Map<String, String> filter = new HashMap<>();
            filter.put(UserRoleDAO.NAME, RoleNames.EMPLOYEE);
            List<String> queriedAttrs = List.of(UserRoleDAO.ID);
            EntityResult roleQueryEntityResult = daoHelper.query(userRoleDAO, filter, queriedAttrs);
            Integer employeeRoleId = (Integer) roleQueryEntityResult.getRecordValues(0).get(UserRoleDAO.ID);
            String specificRoleName = (String) attrMap.get(UserDAO.EMPLOYEE_ROLE);

            // TODO: ufffffffffff.... quitar isto pronto, uso de enums
            if (
                    specificRoleName.equals(RoleNames.CLIENT)
                            || specificRoleName.equals(RoleNames.MANAGER)
                            || specificRoleName.equals(RoleNames.GUEST)
                            || specificRoleName.equals(RoleNames.ROOT)
            ) {
                throw new Exception("Invalid employee type");
            }

            filter.put(UserRoleDAO.NAME, specificRoleName);
            roleQueryEntityResult = daoHelper.query(userRoleDAO, filter, queriedAttrs);
            Integer specificRoleId = (Integer) roleQueryEntityResult.getRecordValues(0).get(UserRoleDAO.ID);

            attrMap.put(UserDAO.ROLE_IDS, List.of(employeeRoleId, specificRoleId));

            validateDaysOff(daysOff, days);

            result = userService.userInsert(attrMap);

            Map<String, String> daysOffToInsert = new HashMap<>();
            daysOffToInsert.put(UsersDaysOffDAO.LOGIN_NAME, (String) attrMap.get(UserDAO.LOGIN_NAME));

            for (String dayOff : daysOff) {
                daysOffToInsert.put(UsersDaysOffDAO.DAY, dayOff);
                daoHelper.insert(this.usersDaysOffDAO, daysOffToInsert);
            }

            if (result.getCode() == EntityResult.OPERATION_WRONG) {
                return result;
            }

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

    private void validateDaysOff(Set<String> daysOff, Set<String> days) throws InvalidShiftException {
        if (daysOff != null) {
            if (daysOff.isEmpty()) {
                throw new InvalidShiftException(IShiftService.NO_DAYS_OFF);
            }

            if (!days.containsAll(daysOff)) {
                throw new InvalidShiftException(IShiftService.INVALID_DAY_OFF);
            }

            if (daysOff.containsAll(days)) {
                throw new InvalidShiftException(IShiftService.ALL_DAYS_OFF);
            }
        } else {
            throw new InvalidShiftException(IShiftService.NO_DAYS_OFF);
        }
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
